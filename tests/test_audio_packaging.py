#!/usr/bin/env python3
"""Integration test of optional asset packaging; fixtures are not learning audio."""
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import zipfile

root = Path(__file__).resolve().parents[1]
scratch = Path('/opt/data/cache/scratch')
with tempfile.TemporaryDirectory(prefix='audio-package-', dir=scratch) as d:
    work = Path(d)
    shutil.copytree(root / 'app', work / 'app')
    shutil.copy2(root / 'build-debug.sh', work / 'build-debug.sh')
    clip = work / 'app/src/main/assets/audio/nam/2/c4836e.mp3'
    clip.parent.mkdir(parents=True, exist_ok=True)
    clip.write_bytes(b'not audio; package fixture only')
    subprocess.run(['docker', 'run', '--rm', '-v', '/home/yes/.hermes:/opt/data',
        '-w', str(work), '-e', 'ANDROID_HOME=/opt/data/cache/scratch/android-build-probe/sdk',
        'eclipse-temurin:21-jdk', 'sh', '-c',
        f'trap "chown -R {os.getuid()}:{os.getgid()} build" EXIT; sh build-debug.sh'], check=True)
    with zipfile.ZipFile(work / 'build/manual/app-debug.apk') as apk:
        entry = apk.getinfo('assets/audio/nam/2/c4836e.mp3')
        assert entry.compress_type == zipfile.ZIP_STORED, 'Clip must support Android openFd()'
        assert apk.read(entry) == clip.read_bytes(), 'Fixture must be packaged unchanged'
print('PASS: optional clip bundled uncompressed in signed APK')
