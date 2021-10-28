import argparse
import os
import re
import subprocess
import sys

import requests

class Namespace(object):
    def __init__(self, **kw):
        self.__dict__.update(kw)

parser = argparse.ArgumentParser()

parser.add_argument("--webhook", action="append")
parser.add_argument("--serverupdate", action="append")

if not sys.argv[1].startswith("-"):
    args = Namespace(webhook=[sys.argv[1]], serverupdate=[sys.argv[2]])
else:
    args = parser.parse_args()

print("Current directory: " + os.getcwd())

gittags = subprocess.check_output("git describe --tags").strip().decode()
m = re.match("^([0-9.]+)\\+([0-9.]+)((?:-g[0-9]+-[0-9a-f]+)?(?:-dirty)?)$", gittags)
file = f"pswg-{m.group(1)}{m.group(3)}+{m.group(2)}.jar"

print("Trying to upload", os.path.realpath(file))
if os.path.isfile(file):
    print("Found file")
    for url in args.webhook:
        with open(file, "rb") as fp:
            with requests.post(url, files={"file": fp}, data={"content": f"Build {os.environ['BUILD_NUMBER']} built! <:mc_cake:711406798292254792>"}) as resp:
                print("discord response:", resp.text)
    for url in args.serverupdate:
        subprocess.check_call(["curl", "--location", "--resolve", "kb1000.de:443:10.244.44.214", "--request", "POST", url, "--form", f"file=@{file}"])
else:
    print("Did not find file")
    with requests.post(sys.argv[1], data={"content": f"Build {os.environ['BUILD_NUMBER']} failed! \N{CROSS MARK}"}) as resp:
        print("discord response:", resp.text)
