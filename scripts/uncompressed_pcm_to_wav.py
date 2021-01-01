# Huge thanks to zenchreal, the author of ealayer3, for writing this decoder!

import struct
import sys
import wave

class FileFormatError(Exception):
    pass

def read_chunk(f, expect_type=[]):
    header = f.read(4)
    if len(header) == 0:
        return (0, b'')
    elif len(header) != 4:
        raise FileFormatError('not enough bytes available for chunk header')
    chunk_type, size = struct.unpack('>HH', header)
    if chunk_type not in expect_type:
        raise FileFormatError('unexpected chunk type 0x{:x}'.format(chunk_type))
    if size < 4:
        raise FileFormatError('chunk size is too small')
    return (chunk_type, f.read(size - 4))

def byte_swap_array(data):
    result = bytearray(data)
    for i in range(0, len(result), 2):
        temp = result[i]
        result[i] = result[i + 1]
        result[i + 1] = temp
    return result

def skip_pre_header(f):
    offset = f.tell()
    done = False
    while not done:
        data = f.read(8)
        if data == bytearray.fromhex('0110000000000014'):
            # I don't know much about this header, so we're just going to skip it
            offset += 0x30
        else:
            done = True
        f.seek(offset)

def decode_file(filename):
    with open(filename, 'rb') as f:
        skip_pre_header(f)
        index = 1
        while True:
            # Initial header parsing
            _, header = read_chunk(f, [0x4800])
            if not header and index > 1:
                break
            if len(header) != 8:
                raise FileFormatError('unknown initial header size {}'.format(len(header)))
            compression, flags, sample_rate = struct.unpack('>BBHxxxx', header)
            if compression != 0x12:
                raise FileFormatError('compression 0x{:x} not recognized (only 0x12 is supported by this tool)'.format(compression))
            if flags & 4:
                channels = 2
            else:
                channels = 1

            # Resulting wave file
            out_filename = '{}_{}.wav'.format(filename, index)
            with wave.open(out_filename, 'wb') as wav_out:
                wav_out.setsampwidth(2)
                wav_out.setnchannels(channels)
                wav_out.setframerate(sample_rate)
                while True:
                    chunk_type, data = read_chunk(f, [0x4400, 0x4500])
                    if chunk_type == 0x4500:
                        break
                    wav_out.writeframes(byte_swap_array(data[4:]))
            index += 1

if __name__ == "__main__":
    args = sys.argv[1:]
    if not args:
        print('Please pass some files to decode.')
        sys.exit(1)
    else:
        for filename in args:
            try:
                decode_file(filename)
            except FileNotFoundError:
                print('{}: file not found'.format(filename))
            except FileFormatError as e:
                print('{}: {}'.format(filename, str(e)))
