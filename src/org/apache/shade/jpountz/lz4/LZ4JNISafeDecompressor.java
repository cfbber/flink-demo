package org.apache.shade.jpountz.lz4;

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

import org.apache.shade.jpountz.util.Utils;

import static org.apache.shade.jpountz.util.Utils.checkRange;

/**
 * {@link org.apache.shade.jpountz.lz4.LZ4SafeDecompressor} implemented with JNI bindings to the original C
 * implementation of LZ4.
 */
final class LZ4JNISafeDecompressor extends org.apache.shade.jpountz.lz4.LZ4SafeDecompressor {

  public static final LZ4SafeDecompressor INSTANCE = new LZ4JNISafeDecompressor();

  @Override
  public final int decompress(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff, int maxDestLen) {
    Utils.checkRange(src, srcOff, srcLen);
    Utils.checkRange(dest, destOff, maxDestLen);
    final int result = org.apache.shade.jpountz.lz4.LZ4JNI.LZ4_decompress_safe(src, srcOff, srcLen, dest, destOff, maxDestLen);
    if (result < 0) {
      throw new LZ4Exception("Error decoding offset " + (srcOff - result) + " of input buffer");
    }
    return result;
  }
}