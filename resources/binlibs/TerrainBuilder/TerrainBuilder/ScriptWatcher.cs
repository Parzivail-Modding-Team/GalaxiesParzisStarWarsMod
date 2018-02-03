using System;
using System.IO;
using System.Threading;
using MoonSharp.Interpreter;
using PFX;

namespace TerrainBuilder
{
    class ScriptWatcher
    {
        private int _stringHash;
        private FileSystemWatcher _watcher;

        public delegate void FileChangedEventHandler(object sender, ScriptChangedEventArgs e);
        public event FileChangedEventHandler FileChanged;

        public void WatchTerrainScript(string filename)
        {
            filename = Path.GetFullPath(filename);
            // Create a new FileSystemWatcher and set its properties.
            _watcher?.Dispose();
            _watcher = new FileSystemWatcher
            {
                Path = Path.GetDirectoryName(filename),
                NotifyFilter = NotifyFilters.LastWrite | NotifyFilters.FileName | NotifyFilters.DirectoryName,
                Filter = Path.GetFileName(filename)
            };

            // Add event handlers.
            _watcher.Changed += OnChanged;
            _watcher.Deleted += OnDeleted;
            _watcher.Renamed += OnRenamed;

            // Begin watching.
            _watcher.EnableRaisingEvents = true;
            LoadScript(filename);
        }

        private static FileStream WaitForFile(string fullPath, FileMode mode, FileAccess access, FileShare share)
        {
            for (var numTries = 0; numTries < 10; numTries++)
            {
                FileStream fs = null;
                try
                {
                    fs = new FileStream(fullPath, mode, access, share);
                    return fs;
                }
                catch (IOException)
                {
                    fs?.Dispose();
                    Thread.Sleep(50);
                }
            }

            return null;
        }

        private void OnRenamed(object sender, RenamedEventArgs e)
        {
        }

        private void OnDeleted(object sender, FileSystemEventArgs e)
        {
        }

        private void OnChanged(object sender, FileSystemEventArgs e)
        {
            LoadScript(e.FullPath);
        }

        private void LoadScript(string filename)
        {
            var fs = WaitForFile(filename, FileMode.Open, FileAccess.Read, FileShare.Read);
            if (fs == null)
            {
                Lumberjack.Error("Failed to gain exclusive lock to read script!");
                return;
            }

            string scriptCode;
            using (var reader = new StreamReader(fs))
            {
                scriptCode = reader.ReadToEnd();
            }

            if (scriptCode.GetHashCode() == _stringHash)
                return;

            _stringHash = scriptCode.GetHashCode();
            var script = new Script
            {
                Options =
                {
                    DebugPrint = s => { Lumberjack.Log(s, ConsoleColor.Cyan, "LUA"); }
                }
            };

            OnFileChanged(new ScriptChangedEventArgs
            {
                Script = script,
                ScriptCode = scriptCode,
                Filename = filename
            });
        }

        protected virtual void OnFileChanged(ScriptChangedEventArgs e)
        {
            FileChanged?.Invoke(this, e);
        }

        public int GetScriptId()
        {
            return _stringHash;
        }
    }

    internal class ScriptChangedEventArgs : EventArgs
    {
        public Script Script { get; set; }
        public string ScriptCode { get; set; }
        public string Filename { get; set; }
    }
}
