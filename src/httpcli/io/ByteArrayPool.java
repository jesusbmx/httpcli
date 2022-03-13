/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package httpcli.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * ByteArrayPool is a source and repository of
 * <code>byte[]</code> objects. Its purpose is to supply those buffers to
 * consumers who need to use them for a short period of time and then dispose of
 * them. Simply creating and disposing such buffers in the conventional manner
 * can considerable heap churn and garbage collection delays on Android, which
 * lacks good management of short-lived heap objects. It may be advantageous to
 * trade off some memory in the form of a permanently allocated pool of buffers
 * in order to gain heap performance improvements; that is what this class does.
 * <p> A good candidate user for this class is something like an I/O system that
 * uses large temporary
 * <code>byte[]</code> buffers to copy data around. In these use cases, often
 * the consumer wants the buffer to be a certain minimum size to ensure good
 * performance (e.g. when copying data chunks off of a stream), but doesn't mind
 * if the buffer is larger than the minimum. Taking this into account and also
 * to maximize the odds of being able to reuse a recycled buffer, this class is
 * free to return buffers larger than the requested size. The caller needs to be
 * able to gracefully deal with getting buffers any size over the minimum. <p>
 * If there is not a suitably-sized buffer in its recycling pool when a buffer
 * is requested, this class will allocate a new buffer and return it. <p> This
 * class has no special ownership of buffers it creates; the caller is free to
 * take a buffer it receives from this pool, use it permanently, and never
 * return it to the pool; additionally, it is not harmful to return to this pool
 * a buffer that was allocated elsewhere, provided there are no other lingering
 * references to it. <p> This class ensures that the total size of the buffers
 * in its recycling pool never exceeds a certain byte limit. When a buffer is
 * returned that would cause the pool to exceed the limit, least-recently-used
 * buffers are disposed.
 */
public class ByteArrayPool {
  /** El grupo de búferes, organizado tanto por el último uso como por el tamaño del búfer */
  private List<byte[]> mBuffersByLastUse = new LinkedList<byte[]>();
  private List<byte[]> mBuffersBySize = new ArrayList<byte[]>(64);
  
  /** El tamaño total de los almacenamientos intermedios en el grupo */
  private int mCurrentSize = 0;
  
  /**
   * El tamaño máximo agregado de los almacenamientos intermedios en el conjunto. 
   * Los buffers antiguos se descartan para permanecer debajo de este límite.
   */
  private final int mSizeLimit;
  
  /** Compara los almacenamientos intermedios por tamaño */
  protected static final Comparator<byte[]> BUF_COMPARATOR = new Comparator<byte[]>() {
    @Override public int compare(byte[] lhs, byte[] rhs) {
      return lhs.length - rhs.length;
    }
  };

  /**
   * @param sizeLimit the maximum size of the pool, in bytes
   */
  public ByteArrayPool(int sizeLimit) {
    mSizeLimit = sizeLimit;
  }

  /**
   * Devuelve un búfer del grupo si hay uno disponible en el tamaño solicitado
   * o asigna uno nuevo si uno agrupado no está disponible.
   *
   * @param len el tamaño mínimo, en bytes, del buffer requerido. los
   * el buffer devuelto puede ser más grande.
   * @return byte[] el buffer siempre es devuelto
   */
  public synchronized byte[] getBuf(int len) {
    for (int i = 0; i < mBuffersBySize.size(); i++) {
      byte[] buf = mBuffersBySize.get(i);
      if (buf.length >= len) {
        mCurrentSize -= buf.length;
        mBuffersBySize.remove(i);
        mBuffersByLastUse.remove(buf);
        return buf;
      }
    }
    return new byte[len];
  }

  /**
   * Devuelve un búfer al grupo, descartando búferes antiguos si el grupo lo 
   * haría excede su tamaño asignado.
   *
   * @param buf el búfer para volver al grupo.
   */
  public synchronized void returnBuf(byte[] buf) {
    if (buf == null || buf.length > mSizeLimit) {
      return;
    }
    mBuffersByLastUse.add(buf);
    int pos = Collections.binarySearch(mBuffersBySize, buf, BUF_COMPARATOR);
    if (pos < 0) {
      pos = -pos - 1;
    }
    mBuffersBySize.add(pos, buf);
    mCurrentSize += buf.length;
    trim();
  }

  /**
   * Elimina los almacenamientos intermedios de la agrupación hasta que 
   * esté por debajo de su límite de tamaño.
   */
  private synchronized void trim() {
    while (mCurrentSize > mSizeLimit) {
      byte[] buf = mBuffersByLastUse.remove(0);
      mBuffersBySize.remove(buf);
      mCurrentSize -= buf.length;
    }
  }
}
