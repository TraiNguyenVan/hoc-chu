# Specification — Bé học chữ

Status: draft, reconstructed from the project conversation; implementation is incomplete. Do not treat unverified details below as completed or approved requirements.

## Goal and audience

A native Android learning app for Vietnamese first-grade pupils to learn chữ cái, chữ ghép and vần. It is for the user's personal use, not public distribution. It must work well on the user's connected Samsung tablet as well as adapt to other screen sizes.

## Source and visual direction

- Primary reference is the user's replacement chart `/opt/data/attachments/image_226314-2.webp`, a digital image without glare. The older photograph `/opt/data/attachments/2aOboRFakXFyIMnrenha9hV6V2j6MfTiofcexk4e-2.jpg` is supporting context only; its glare is not a blocker and is not a reason to ask the user for another image.
- Include **every character and vần on the chart**, with no omissions. Reconcile each cell, its diacritics, category and order against the image; do not infer uncertain cells silently. Keep an independently checked reference inventory and an exact comparison test. This verification is not complete.
- Make the user-provided chart the primary learning experience, not merely a palette for a generic dashboard: a unified, ordered board with its gold title, pink floral framing, thin green grid on white, red letters/chữ ghép and blue vần. Preserve the chart's grouping and reading order while adapting cells and scrolling to touch. Search/filter and practice are secondary affordances; do not replace the board with separate disconnected category-only screens.
- Tablet-first responsive layout, legible Vietnamese diacritics, clear hierarchy, accessible contrast and large touch targets; preserve usability in portrait and landscape.

## Functional requirements

1. Browse the complete chart, organized so pupils can find chữ cái, chữ ghép and vần. Tapping an item opens a legible enlarged view.
2. Provide accurate audio for each item, with a clear way to replay it; learning audio must be appropriate for teaching first grade, not merely sound natural. Distinguish letter names from sounds used for spelling. An educator or other qualified reviewer must validate the recordings and examples before presenting them as authoritative.
3. Show appropriate, verified examples where available; never invent an example to fill a gap.
4. Provide a practice activity with clear prompts, unambiguous answers, immediate feedback and a result summary. It must work independently of audio where needed and must not claim to assess listening unless audio is actually played.
5. No account or ads for the pupil. The intended baseline is usable offline; determine whether the selected audio can be packaged for offline use under its service terms.

## Audio decision and constraints

- The user selected Viettel voice Quỳnh Anh at speed 0.8, using continuous input such as `a nờ an` without post-processing pauses. Raw `ngh` was read as individual letter names, so use a teaching-sound input such as `ngờ`. The selected voice and sample are approved; the full set and individual pronunciations still need review.
- Viettel's public demo can be used for limited personal evaluation; its product page offers generated MP3 downloads. Personal use does not automatically establish permission for bulk automation or wider distribution, and product-specific terms remain unclear. Avoid unauthorized access and respect service limits. Record provenance and mark clips as provisional until pronunciation is checked. Do not embed secrets.
- Seek a complete, suitable set for private offline use, human-recorded or AI-generated. A natural voice alone does not establish correct spelling instruction; review is still needed.
- The build contains provisional offline Viettel item-level audio. Items without a matching clip must say it is unavailable; do not claim all teaching audio is ready or reviewed.

## Acceptance criteria

- [ ] The complete, verified chart inventory matches every printed cell, including diacritics; an automated comparison fails on missing, extra or altered items.
- [ ] Every item is selectable and legible at tablet and phone sizes in both orientations.
- [ ] Every applicable item plays its own reviewed audio and can be replayed; clearly report an unavailable clip rather than silently playing the wrong one.
- [ ] Verify pronunciation and teaching examples with a qualified human reviewer; document dialect and any decisions.
- [ ] Practice works end-to-end, with correct answers, feedback, score, replay, and no duplicate scoring.
- [ ] Test on the connected Samsung tablet over ADB: install, launch, navigate all sections, tap items, play audio, finish practice, and inspect screenshots in both orientations. Preserve and report test evidence.
- [ ] Test offline behavior, audio playback, app lifecycle/rotation, accessibility and failure cases; record actual outcomes.
- [ ] Build and deliver an installable APK and source/build instructions. Never call the app finished while any of these gates is unresolved.

## Current prototype (verified earlier, not completion)

- Native Android Java prototype; source under `app/src/main/java/vn/khoaikhoai/hocchu/`.
- Debug build script `build-debug.sh`; current APK `build/manual/app-debug.apk`. Gradle wrapper and Android build configuration have also been added; see README.md for prerequisites and tested commands.
- A unified chart interface and a visual matching quiz were installed and exercised on the tablet. The current installed build bundles 185 provisional Viettel clips; chart and teaching correctness remain subject to the checks above.
- `tests/ChartTest.java` compares 185 entries exactly against a candidate transcription; it does **not** prove the fixture matches the photo.

## Open decisions and risks

- Compare entries with the replacement image and corroborating curriculum sources; do not ask for another image because of glare in the older photograph. The candidate row-6 `ơt` is supported by the curriculum guide, though its mark was visually ambiguous in one crop.
- Find or generate a complete, usable spelling set suitable for a private offline app. Confirm cost and reuse conditions before committing to any purchase.
- Arrange qualified review of phonics, examples and spoken output. A smooth AI voice alone does not establish correctness.
- Decide how reviewed sound files are updated and versioned; personal use is the current scope.
