package org.apache.shade.net.jpountz.lz4;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.shade.net.jpountz.util.Utils;

import static org.apache.shade.net.jpountz.util.Utils.checkRange;

/**
 * {@link org.apache.shade.net.jpountz.lz4.LZ4FastDecompressor} implemented with JNI bindings to the original C
 * implementation of LZ4.
 */
final class LZ4JNIFastDecompressor extends LZ4FastDecompressor {

  public static final LZ4JNIFastDecompressor INSTANCE = new LZ4JNIFastDecompressor();

  @Override
  public final int decompress(byte[] src, int srcOff, byte[] dest, int destOff, int destLen) {
    Utils.checkRange(src, srcOff);
    Utils.checkRange(dest, destOff, destLen);
    final int result = org.apache.shade.net.jpountz.lz4.LZ4JNI.LZ4_decompress_fast(src, srcOff, dest, destOff, destLen);
    if (result < 0) {
      throw new LZ4Exception("Error decoding offset " + (srcOff - result) + " of input buffer");
    }
    return result;
  }

}
