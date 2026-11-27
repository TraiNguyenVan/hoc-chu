#!/usr/bin/env python3
"""Exercise the real five-question visual quiz on an authorized Android device.

Default transport is the user's existing ws-scrcpy container. Override ADB_COMMAND
and ANDROID_SERIAL to use another device. This script changes the tablet's active
screen; it does not alter connectivity, system settings, or installed packages.
"""
import os
import re
import shlex
import subprocess
import time
import xml.etree.ElementTree as ET

ADB = shlex.split(os.environ.get('ADB_COMMAND', 'docker exec ws-scrcpy-plus-ws-scrcpy-1 adb'))
SERIAL = os.environ.get('ANDROID_SERIAL', '192.168.2.148:5555')


def adb(*args):
    return subprocess.check_output(ADB + ['-s', SERIAL, *args], text=True, timeout=35)


def snapshot():
    for attempt in range(3):
        try:
            adb('shell', 'uiautomator', 'dump', '/sdcard/hocchu-smoke.xml')
            return ET.fromstring(adb('shell', 'cat', '/sdcard/hocchu-smoke.xml'))
        except (subprocess.CalledProcessError, ET.ParseError):
            if attempt == 2:
                raise
            time.sleep(1)


def nodes(root):
    return list(root.iter('node'))


def center(bounds):
    values = list(map(int, re.findall(r'\d+', bounds)))
    if len(values) != 4 or values[2] <= values[0] or values[3] <= values[1]:
        raise RuntimeError('Invalid visible bounds: ' + bounds)
    return (values[0] + values[2]) // 2, (values[1] + values[3]) // 2


def click(node):
    x, y = center(node.attrib['bounds'])
    adb('shell', 'input', 'tap', str(x), str(y))
    time.sleep(.6)


def find_text(root, text, *, clickable=None):
    found = [n for n in nodes(root) if n.attrib.get('text') == text and
             (clickable is None or (n.attrib.get('clickable') == 'true') == clickable)]
    if not found:
        raise RuntimeError('Missing UI text: ' + text)
    return found


def main():
    if adb('get-state').strip() != 'device':
        raise RuntimeError('Android device is not authorized/connected')
    adb('shell', 'am', 'start', '-n', 'vn.khoaikhoai.hocchu/.MainActivity')
    time.sleep(1)
    click(find_text(snapshot(), 'Luyện tập', clickable=True)[0])
    root = snapshot()
    # The quiz category card is clickable; its TextView child is not.
    click(find_text(root, 'Chữ cái', clickable=False)[-1])
    for q in range(1, 6):
        root = snapshot()
        find_text(root, f'CÂU {q} / 5')
        candidates = [n for n in nodes(root) if n.attrib.get('text') and
                      n.attrib.get('clickable') == 'true' and
                      len(n.attrib['text']) <= 3 and n.attrib['text'] not in ('✿',)]
        labels = {n.attrib['text'] for n in candidates}
        target = [n.attrib['text'] for n in nodes(root) if n.attrib.get('text') in labels and
                  n.attrib.get('clickable') != 'true']
        if len(target) != 1:
            raise RuntimeError(f'Cannot uniquely identify question target: {target}')
        answer = [n for n in candidates if n.attrib['text'] == target[0]]
        if len(answer) != 1:
            raise RuntimeError(f'Cannot uniquely identify answer {target[0]}')
        click(answer[0])
        # On a landscape tablet the feedback and button may start below the viewport.
        adb('shell', 'input', 'swipe', '600', '735', '600', '360', '450')
        time.sleep(.4)
        root = snapshot()
        find_text(root, 'Đúng rồi, giỏi quá!')
        click(find_text(root, 'Tiếp theo  →', clickable=True)[0])
        print(f'PASS question {q}: {target[0]}')
    root = snapshot()
    find_text(root, 'Bé đã hoàn thành!')
    find_text(root, '5 / 5')
    print('PASS quiz completed with score 5 / 5')


if __name__ == '__main__':
    main()
