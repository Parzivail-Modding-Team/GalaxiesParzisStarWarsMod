using Substrate.Core;
using Substrate.Nbt;

namespace GenerateChunkDiff
{
    public class ChunkSchematic
    {
        public int Width;
        public int Height;
        public int Length;

        public short[] Blocks;
        public byte[] Metadatas;

        public ChunkSchematic(string schematic)
        {
            var file = new NBTFile(schematic + ".schematic");
            var tag = new NbtTree(file.GetDataInputStream()).Root;

            Width = tag["Width"].ToTagShort().Data;
            Height = tag["Height"].ToTagShort().Data;
            Length = tag["Length"].ToTagShort().Data;

            // read in block data; Vanilla lower byte array
            LoadBlocks(tag);
        }

        public void Save()
        {

        }

        private void LoadBlocks(TagNodeCompound tag)
        {
            byte[] bLower = tag["Blocks"].ToTagByteArray().Data;

            byte[] addBlocks = new byte[0];
            // Check and load Additional blocks array
            if (tag.ContainsKey("AddBlocks"))
            {
                addBlocks = tag["AddBlocks"].ToTagByteArray().Data;
            }

            Blocks = new short[bLower.Length];
            Metadatas = tag["Data"].ToTagByteArray().Data;

            for (var index = 0; index < bLower.Length; index++)
            {
                short n;
                if (index >> 1 >= addBlocks.Length)
                    n = (short) (bLower[index] & 0xFF);
                else if ((index & 1) == 0)
                    n = (short) (((addBlocks[index >> 1] & 0x0F) << 8) + (bLower[index] & 0xFF));
                else
                    n = (short) (((addBlocks[index >> 1] & 0xF0) << 4) + (bLower[index] & 0xFF));
                Blocks[index] = n;
            }
        }

        public short GetBlockAt(int x, int y, int z)
        {
            //int i = (y * length + z) * width + x;
            //return (i >= size()) ? null : blocks[i];
            return Blocks[(y*Length + z)*Width + x];
        }

        public byte GetMetadataAt(int x, int y, int z)
        {
            //int i = (y * length + z) * width + x;
            //return (i >= size()) ? null : blocks[i];
            return Metadatas[(y*Length + z)*Width + x];
        }
    }
}