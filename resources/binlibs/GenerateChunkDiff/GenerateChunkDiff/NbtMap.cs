using System.Collections.Generic;
using Substrate.Core;
using Substrate.Nbt;

namespace GenerateChunkDiff
{
    internal class NbtMap : Dictionary<int, string>
    {
        public static NbtMap Load(string filename)
        {
            var map = new NbtMap();

            var nf = new NBTFile(filename);

            using (var nbtstr = nf.GetDataInputStream())
            {
                var tree = new NbtTree(nbtstr);

                var root = tree.Root["map"];
                var list = root.ToTagList();

                foreach (var tag in list)
                {
                    var k = tag.ToTagCompound()["k"].ToTagString();
                    var v = tag.ToTagCompound()["v"].ToTagInt();
                    if (!map.ContainsKey(v))
                        map.Add(v, k);
                }

                return map;
            }
        }
    }
}