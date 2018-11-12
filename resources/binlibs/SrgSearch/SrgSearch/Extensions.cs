using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SrgSearch
{
    static class Extensions
    {
        public static string ReadUntil(this TextReader reader, params char[] delimiters)
        {
            var chars = new List<char>();
            while (reader.Peek() >= 0)
            {
                var c = (char)reader.Read();
                if (delimiters.Contains(c))
                    break;
                chars.Add(c);
            }
            return new string(chars.ToArray());
        }
    }
}
