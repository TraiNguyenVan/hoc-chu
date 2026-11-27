# Chart inventory: candidate, image and guide comparisons

`chart-reference.tsv` is a **provisional transcription**, not a verified ground truth. Rows are top-to-bottom, occupied cells left-to-right, one-based. It records all 185 candidate printed entries, grouped by *visual chart section*: 29 red alphabet entries (rows 1–2), 14 entries on the red third row (row 3), and 142 blue entries (rows 4–14). There are no blank-cell records. The intended row widths are specified by `ChartTest` so truncated or malformed fixture rows fail.

The original supplied photograph is `/opt/data/attachments/2aOboRFakXFyIMnrenha9hV6V2j6MfTiofcexk4e-2.jpg` (2560×1920, SHA-256 `82b9d19546237bdde768ca3e07261325bd777f9e7db9c15bbc89a0b52f440fbb`). The supplied 600×600 webp `/opt/data/attachments/image_226314-2.webp` (SHA-256 `c28bfe728fa5e61bc3abb4f7eb49b5f7ec7a48c35da5a7e7e39cd9eaa119c5ec`) was consulted separately. Transcription was compared across overlapping original-image crops, e.g. [100,200,2450,800], [100,750,2450,1250], [100,1200,2450,1800], and targeted crops around glare and bottom row. Crop coordinates are original-image pixels in x1,y1,x2,y2 order. Vision-model readings are inconsistent and are not an independent human verification. Do not use repeated model agreement as proof.

## Replacement-image comparison (latest)

An independent model-assisted pass compared all 185 entries against the user's replacement digital image, `/opt/data/attachments/image_226314-2.webp`, and reported no confirmed differences. Targeted enlarged crops support `iêng`, `yêu`, `ươu`, `uơ` and unmarked `ưc` and `ươc`. Its crop of row 6 column 2 could not distinguish `ơt` from `ớt`; the independent teacher-training guide sequence below says `ơt`. These are model-assisted observations, not a human verification of every glyph or of layout. The replacement image is the primary reference; the old photograph's glare is not a current blocker and is not grounds to request another image.

## Historical questions from the older photograph

- **Row 9, column 13**: candidate `iêng`; glare obscures the end. A smaller crop produced conflicting `iêm` and partial `iê…` readings. A clearer photo or human examination is required.
- **Row 11, columns 12–13**: candidate `yêu`, `ươu`; glare compromises their final letters. Other model readings described partially obscured characters, so they remain unverified.
- **Row 6, column 2**: candidate `ơt`; the photo crop was inconsistently read as `ớt`, while the webp crop gave `ơt`. The possible mark needs human examination rather than normalizing based on linguistic expectations.
- **Row 9, column 12**: candidate `ưc`; a crop was read as `ực`, while broader readings gave `ưc`. Check whether a dot below is printed, a guide-line artifact, or absent.
- **Row 10, column 6**: candidate `ươc`; one model read `ước` and another `ươc`. Confirm any tone mark against a clearer image.
- **Row 14, column 8**: candidate `uơ`; an enlarged crop yielded `ươ`, whereas targeted readings of both sources gave `uơ`. Confirm horn placement with a human/clearer image.
- **Row 14, column 11**: candidate `uâng`; small marks and horn placement are not definitive despite repeated readings.
- Other entries and physical grid divisions also require a qualified human's full cell-by-cell inspection. Because model readings varied even when expressed confidently, this list is not an exhaustive clearance of all other cells.

Preserve apparent chart text rather than silently correcting unusual spellings, but neither the candidates nor the photograph establish pedagogical validity. Category 1 is the *printed red row*, not a linguistic claim that `ia`, `ua`, and `ưa` are consonant digraphs. Audio is category-scoped, so the category reassignment requires any future reviewed clips for these three labels to use category 1.

## Test

From project root, compile and run `ChartTest` with `LearningData.java` on a JDK, e.g.:

```sh
mkdir -p /data/cache/scratch/khoai-chart-test
javac -encoding UTF-8 -d /data/cache/scratch/khoai-chart-test app/src/main/java/vn/khoaikhoai/hocchu/LearningData.java tests/ChartTest.java
java -cp /data/cache/scratch/khoai-chart-test vn.khoaikhoai.hocchu.ChartTest
```

The test checks exact label, order, category and count against the fixture, plus fixture row structure and mutation checks for altered marks, omissions, additions, ordering and moved categories. A pass means **data matches the candidate fixture only**; it does not prove that the fixture matches the photograph, verify the app's rendered grid, or verify teaching content/audio. The app API returns flat categories, so the test cannot establish physical cell coordinates or actual blank cells in the UI.

## Independent comparison against teacher-training guide

The guide explicitly states “Phần Vần gồm 142 vần, được sắp xếp theo trật tự sau:” and prints the complete comma-separated sequence on **PDF page 9 (printed page 58)**.[1] PDF page 8 (printed page 57) also states the learning objective of 142 vần.[1] The PDF text extracted from that sequence matches all **142** category-2 fixture labels in order, with no code-point differences; NFC-normalized comparison also matches. This is a comparison to the guide, not proof of the photograph's text or layout.

The checked PDF's SHA-256 was `914415279f7c0238ba8ed2b4c1208741c86b816e4d50eec70175ebdea83f7715`; the fixture's was `a4c5c674b9f46cd33cdb2dd46bc3e58d2c65cb62575353662ba84b7fda8a8ff3`. The run used Python and `pypdf 6.21.4`. From the project root, with `curl`, Python 3, and `pypdf` installed, reproduce (the downloaded PDF is placed outside the project):

```sh
mkdir -p /opt/data/cache/scratch/chart-guide-check
curl -fL 'https://sachdientu.sachthietbigiaoduc.vn/upload/tai-lieu-tap-huan-sgk-lop-1/tieng-viet/tai-lieu-tap-huan-giao-vien-tieng-viet-1.pdf?v=1.0.4' -o /opt/data/cache/scratch/chart-guide-check/guide.pdf
sha256sum /opt/data/cache/scratch/chart-guide-check/guide.pdf tests/chart-reference.tsv
python - <<'PY'
import csv
import re
import unicodedata
from pathlib import Path
from pypdf import PdfReader

pdf = Path('/opt/data/cache/scratch/chart-guide-check/guide.pdf')
fixture = Path('tests/chart-reference.tsv')
page = PdfReader(str(pdf)).pages[8].extract_text(extraction_mode='layout')
start = 'Phần Vần gồm 142 vần, được sắp xếp theo trật tự sau:'
end = 'Các vần khó (khó đọc, khó viết, tần số xuất hiện ít)'
assert page.count(start) == 1 and page.count(end) == 1
block = page.split(start, 1)[1].split(end, 1)[0]
source = [x.strip() for x in block.strip().removesuffix('.').split(',')]
assert all(re.fullmatch(r'[^\W\d_]+', x, flags=re.UNICODE) for x in source), source
rows = list(csv.reader((line for line in fixture.read_text(encoding='utf-8').splitlines() if line and not line.startswith('#')), delimiter='\t'))
assert all(len(row) == 3 for row in rows)
blue = [token for row, category, cells in rows if category == '2' for token in cells.split()]
assert [int(row) for row, category, _ in rows if category == '2'] == list(range(4, 15))
nfc = lambda seq: [unicodedata.normalize('NFC', s) for s in seq]
source_nfc, blue_nfc = nfc(source), nfc(blue)
print(f'PDF sequence count: {len(source)}; fixture category 2 count: {len(blue)}')
print(f'Raw code-point sequence equal: {source == blue}')
print(f'NFC-normalized sequence equal: {source_nfc == blue_nfc}')
for i in range(max(len(source_nfc), len(blue_nfc))):
    a = source_nfc[i] if i < len(source_nfc) else '<missing>'
    b = blue_nfc[i] if i < len(blue_nfc) else '<missing>'
    if a != b:
        print(f'position {i+1}: PDF={a!r}, fixture={b!r}')
assert len(source) == len(blue) == 142 and source_nfc == blue_nfc
PY
```

Observed output from the comparison:

```text
PDF sequence count: 142; fixture category 2 count: 142
Raw code-point sequence equal: True
NFC-normalized sequence equal: True
```

**Limitations:** The comparison uses the PDF's extracted text layer, not independent human verification of its rendered glyphs. It compares only the ordered category-2 labels, not the red rows, category assignment, physical cell positions, app UI, or audio. The source is a teacher-training guide, not necessarily the specific chart's source; the replacement-image comparison is described above. The successful match does not independently certify the specific image or educational audio.

## Sources

[1] https://sachdientu.sachthietbigiaoduc.vn/upload/tai-lieu-tap-huan-sgk-lop-1/tieng-viet/tai-lieu-tap-huan-giao-vien-tieng-viet-1.pdf?v=1.0.4 — Tài liệu tập huấn giáo viên Tiếng Việt 1
