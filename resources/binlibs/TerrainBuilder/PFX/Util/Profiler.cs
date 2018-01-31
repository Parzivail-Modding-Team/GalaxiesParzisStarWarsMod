using System;
using System.Collections.Generic;

namespace PFX.Util
{
    /// <summary>
    ///     Simple class to profile code
    /// </summary>
    public class Profiler
    {
        private readonly Stack<Tuple<string, DateTime>> _profileNameStack = new Stack<Tuple<string, DateTime>>();
        private readonly Dictionary<string, TimeSpan> _profiles = new Dictionary<string, TimeSpan>();

        /// <summary>
        ///     Resets the current profile and returns the previous profile's results
        /// </summary>
        /// <returns>The previous profile's results</returns>
        public Dictionary<string, TimeSpan> Reset()
        {
            var clone = new Dictionary<string, TimeSpan>(_profiles);
            _profiles.Clear();
            _profileNameStack.Clear();
            return clone;
        }

        /// <summary>
        ///     Begins profiling a new section
        /// </summary>
        /// <param name="key">The section to profile</param>
        public void Start(string key)
        {
            _profileNameStack.Push(new Tuple<string, DateTime>(key, DateTime.Now));
        }

        /// <summary>
        ///     Stops profiling the most recently begin profile
        /// </summary>
        public void End()
        {
            if (_profileNameStack.Count == 0)
                throw new Exception("No profiles available to pop");
            var pair = _profileNameStack.Pop();
            var len = DateTime.Now - pair.Item2;
            if (_profiles.ContainsKey(pair.Item1))
                _profiles[pair.Item1] += len;
            else
                _profiles.Add(pair.Item1, len);
        }
    }
}