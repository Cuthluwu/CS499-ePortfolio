# Behavior-Aware Feedback and Bluetooth Scale Roadmap

## Document Status

This roadmap records a researched product extension for the CS 360 Weight Tracker. It is not a claim that Bluetooth import or adaptive color feedback is already implemented. The completed Milestone Four artifact supplies the database foundation: authenticated account ownership, validated numeric measurements, deterministic ordering, stable row identifiers, progress reporting, and repeatable verification. The roadmap explains how a later iteration could build on that foundation without weakening accessibility, privacy, data integrity, or the credibility of the portfolio.

## Personal Product Motivation

Weight tracking is not emotionally neutral. A plain history table can make slow progress hard to notice, while a severe response to one higher measurement can turn normal fluctuation into discouragement. I wanted the Weight Tracker to feel more personal than a database demonstration, but I did not want it to judge the user or assume that every person has the same health goal. The proposed system therefore makes progress understandable while keeping the user in control of the goal, comparison method, and visual feedback.

For a user who explicitly selects weight loss, a sustained downward trend can activate a green interface state. The overview screen would use a pale green background or progress card, a dark green heading, a check icon, a signed change, and specific text such as, "Your seven-day average moved 0.8 lb toward your goal." If the trend moves upward beyond the selected tolerance, the overview can use a pale red attention state with a trend icon and neutral wording: "Your recent average is 0.6 lb above the comparison period. Review the chart or continue tracking." Red is an attention cue, not punishment. It would never label the person as unsuccessful, trigger an alarm, or imply a diagnosis.

The same logic must work for maintenance and medically directed weight-gain goals. A stable range is progress for maintenance, and an upward trend may be progress for a gain goal. If the user has not selected a goal, the application should show neutral measurements rather than assigning a positive or negative state. The user can disable adaptive colors, choose a different palette, hide trend judgments, or return to a neutral interface at any time.

## Psychological Basis and Limits

The behavioral basis is positive reinforcement of consistent self-monitoring, not a claim that the color green independently causes weight loss. In behavioral psychology, reinforcement concerns whether a consequence following a behavior makes that behavior more likely to occur again. The behavior this design should reinforce is returning to record or review measurements. The application should not try to reinforce weight loss by rewarding a lower number every day, because daily weight is affected by hydration, food, clothing, medication, time of measurement, and other factors outside a simple behavior-response chain.

Butryn et al. (2007) describe weight monitoring as an opportunity for positive reinforcement when behavior changes correspond with weight-control progress. Their National Weight Control Registry analysis also found greater one-year weight gain among participants whose self-weighing frequency decreased. Burke et al. (2011) reviewed behavioral weight-loss studies and found that more frequent self-monitoring was generally associated with stronger outcomes. Steinberg et al. (2013) reported clinically meaningful results from an intervention centered on daily self-weighing with smart-scale and electronic feedback. These studies support consistent self-monitoring and visible progress; they do not support shame, universal goal assumptions, or diagnosis by color.

The more recent review by Krukowski et al. (2024a) is important because it prevents the proposal from overstating the evidence. Across 19 studies, results for feedback format and generation were mixed, and weight outcomes were too heterogeneous for a pooled conclusion. A published correction reported a smaller but still significant effect of feedback on physical activity (Krukowski et al., 2024b). Therefore, the portfolio presents adaptive feedback as a researched design hypothesis that requires usability and outcome testing, not as a guaranteed clinical intervention.

This distinction changes the design. The green state acknowledges an interpretable trend and the continued act of tracking. The red state identifies a review condition without moral judgment. Both states offer a next action. Neither state replaces medical guidance or tells the user what weight is healthy.

## Goal-Aware Trend Model

The default classifier should not compare only the newest value with the value immediately before it. That rule is simple, but it is vulnerable to ordinary day-to-day variation. The proposed default compares the mean of the most recent seven calendar days containing measurements with the mean of the preceding seven-day period. The exact window, minimum sample count, and tolerance must be documented and configurable.

The domain service would accept the following inputs:

1. authenticated user identifier;
2. goal direction: loss, maintenance, gain, or no directional goal;
3. current and comparison date windows;
4. normalized measurements in the user's preferred display unit;
5. a user-selected or documented default tolerance; and
6. whether adaptive feedback is enabled.

The service would return a semantic result rather than a color name:

- `PROGRESS`: the trend moved toward the selected goal beyond the tolerance;
- `ATTENTION`: the trend moved away from the selected goal beyond the tolerance;
- `STEADY`: the difference remains inside the tolerance;
- `INSUFFICIENT_DATA`: one or both windows lack the minimum number of measurements;
- `NO_DIRECTIONAL_GOAL`: measurements can be shown, but no directional judgment is appropriate; or
- `ADAPTIVE_FEEDBACK_DISABLED`: the user requested a neutral presentation.

The result should also include the signed delta, sample counts, comparison dates, and a human-readable explanation. The Activity or ViewModel can then map the semantic state to theme tokens, icons, accessibility descriptions, and wording. Keeping classification separate from presentation prevents a color change from silently changing the algorithm.

## Interface State Specification

The first implementation should change the overview area rather than flood every application screen with a saturated color. Recommended accessible tokens are:

- progress background `#E8F5E9` with dark green text `#1B5E20`;
- attention background `#FDECEC` with dark red text `#8B1E1E`; and
- neutral text `#1F2937` on either pale background.

The dark green/background pair has an approximate 7.00:1 contrast ratio, and the dark red/background pair has an approximate 7.99:1 ratio. Both exceed the WCAG 2.2 Level AA 4.5:1 requirement for normal text. Contrast alone is not enough. WCAG Success Criterion 1.4.1 also requires another visual means when color conveys information. Every state must include a heading, icon, signed numeric difference, explanatory sentence, and accessible content description. The design also needs a color-blind-safe alternative and a neutral mode.

Example feedback is deliberately factual:

- Progress: "Trend moving toward your goal. Your seven-day average is 0.8 lb lower than the previous period."
- Attention: "Trend needs review. Your seven-day average is 0.6 lb above the previous period."
- Steady: "Trend is within your maintenance range."
- Insufficient data: "Keep tracking. At least three measurements are needed in each comparison period."

The application should never use messages such as "You failed," "Bad week," or "You gained too much." It should not send repeated negative notifications, display public badges, or share measurements by default. A user who finds adaptive feedback stressful must be able to disable it without losing access to the history and reporting features.

## Commercial and Standards Precedent

Withings provides a real product precedent for the user experience. Its Body+ support documentation states that measurements automatically synchronize to the Withings application after a weigh-in, using Wi-Fi or Bluetooth depending on configuration. This demonstrates that automatic scale-to-app capture is a product category users already understand.

That precedent does not prove that Madison's Weight Tracker can directly read a Withings scale. The Withings workflow may depend on vendor-controlled pairing, protocols, accounts, or services. Direct interoperability would require an officially documented interface, an approved vendor integration, or a device that implements an open standard. The portfolio must not convert a commercial user experience into an unsupported technical claim.

The Bluetooth Special Interest Group's Weight Scale Service version 1.0.1 provides a standards-based direction. The service is intended for consumer healthcare and sports/fitness scales and can expose weight plus optional timestamp, user ID, BMI, and height fields. A device adapter could first detect whether a candidate scale exposes the standard service and measurement characteristic. A vendor-specific adapter would be a separate implementation with separate documentation and acceptance testing.

## Proposed Android Architecture

The extension should separate six responsibilities:

1. **Pairing coordinator:** Starts only after the user chooses **Connect a Scale**, displays candidate devices, records consent, and supports disconnecting.
2. **Device adapter:** Detects a supported standard or vendor interface and parses the measurement payload without writing directly to the database.
3. **Normalization service:** Converts kilograms or pounds into the application's canonical storage unit and applies the same weight range rules used for manual entries.
4. **Import repository:** Writes owner, measurement, source, device, external identifier, and timestamp atomically while preventing duplicates.
5. **Trend service:** Calculates one of the semantic states from stored time-bounded measurements and the user's goal preferences.
6. **Presentation mapper:** Converts semantic state into color tokens, icons, text, and content descriptions without duplicating trend calculations.

On Android 12 and later, `BLUETOOTH_SCAN` and `BLUETOOTH_CONNECT` are runtime permissions. They must be requested at the point the user initiates pairing, with an explanation of why Nearby devices access is needed. On supported Android versions, Companion Device Manager can perform initial device discovery on behalf of the application and reduce location-permission exposure. The exact approach must be selected from the application's minimum SDK, supported hardware, and device protocol rather than copied blindly from a tutorial.

## Database Extension

A production implementation should not overload the current weight-entry fields with device-management details. One defensible schema extension is:

- `connected_devices`: stable local device ID, authenticated `user_id`, display alias, protocol type, protected vendor identifier, connected date, last-sync date, and active flag;
- `weight_entries`: existing owner and weight fields plus `source_type`, `measured_at`, nullable `device_id`, and nullable `external_measurement_id`; and
- optional `sync_events`: non-sensitive success/error category and timestamp for troubleshooting, with a short retention period.

A unique constraint on `(user_id, device_id, external_measurement_id)` would make import idempotent when a device or operating system repeats a notification. If the protocol does not provide a stable measurement identifier, the adapter needs a documented deduplication key based on a protected device identity, measurement timestamp, normalized value, and a narrow collision policy. Deduplication must not silently merge two legitimate measurements taken close together.

The import transaction would validate the authenticated owner, verify that the device belongs to that owner, normalize the measurement, reject malformed or out-of-range data, attempt the idempotent insert, and commit before triggering trend recalculation. A parsing or database failure should leave no partially created device measurement. Manual entries remain supported and continue through the same validation boundary.

## Security, Privacy, and Failure Analysis

Bluetooth expands the attack and privacy surface. Scanning should occur only after an explicit action and for a limited period. The interface should identify the selected device and require confirmation instead of silently choosing the strongest signal. Unsupported payloads, impossible lengths, invalid flags, out-of-range values, and unexpected units must be rejected before persistence. A replayed measurement should be detected by the idempotency rule.

Pairing a device does not authorize one account to see another account's measurements. Every device and imported entry remains scoped to the authenticated user ID. If a shared household scale supplies its own user ID, the app must map that identifier only after explicit user confirmation and must handle unassigned measurements without guessing. Raw hardware addresses should not be retained unless technically unavoidable. A protected stable identifier or non-sensitive alias is preferable.

Users need a visible disconnect control, a plain-language explanation of stored information, and a way to delete imported entries. Background collection should not be enabled silently. The existing local database is not presented as encrypted storage, so sensitive-data protection at rest remains a documented limitation for a production version.

## Verification and Acceptance Plan

The roadmap defines evidence before implementation so completion cannot be claimed from source presence alone.

**Domain tests** would cover loss, maintenance, gain, and no-goal modes; tolerance boundaries; exactly enough and insufficient samples; unit conversion; same-day duplicate measurements; time-zone transitions; and adaptive-feedback-disabled behavior.

**Repository tests** would verify ownership, foreign keys, source metadata, device activation status, duplicate prevention, transaction rollback, ordered history, account deletion behavior, and migration from the current version-two schema.

**Adapter tests** would use non-personal fixtures for standard Weight Scale Service packets, optional timestamp/user/BMI fields, unsupported flags, truncated packets, unexpected units, out-of-range measurements, repeated packets, and vendor-specific rejection.

**Interface and accessibility tests** would confirm that each state remains understandable in grayscale, all normal text meets contrast requirements, focus order remains logical, content descriptions explain the same result, and users can disable adaptive feedback.

**Physical-device acceptance tests** would record scale model, firmware, phone model, Android version, protocol, pairing path, expected reading, imported reading, latency, duplicate behavior, reconnect behavior, and permission denial/recovery. Emulator tests can verify parsing, persistence, and interface state, but they cannot prove radio interoperability.

## Capstone Value

This extension connects database engineering to a real user experience. It requires the developer to coordinate wireless communication, versioned persistence, idempotency, authorization, accessibility, privacy, behavior-aware language, and layered testing. More importantly, it shows judgment: the design uses research without presenting a color as treatment, uses a commercial scale as precedent without claiming unsupported vendor access, and distinguishes a tested database foundation from a future hardware integration.

## References

Android Developers. (2026a). *Bluetooth permissions*. https://developer.android.com/develop/connectivity/bluetooth/bt-permissions

Android Developers. (2026b). *Companion device pairing*. https://developer.android.com/develop/connectivity/bluetooth/companion-device-pairing

Bluetooth Special Interest Group. (2024). *Weight Scale Service* (Version 1.0.1). https://www.bluetooth.com/wp-content/uploads/Files/Specification/HTML/WSS_v1.0.1/out/en/index-en.html

Burke, L. E., Wang, J., & Sevick, M. A. (2011). Self-monitoring in weight loss: A systematic review of the literature. *Journal of the American Dietetic Association, 111*(1), 92-102. https://doi.org/10.1016/j.jada.2010.10.008

Butryn, M. L., Phelan, S., Hill, J. O., & Wing, R. R. (2007). Consistent self-monitoring of weight: A key component of successful weight loss maintenance. *Obesity, 15*(12), 3091-3096. https://doi.org/10.1038/oby.2007.368

Krukowski, R. A., Denton, A. H., & König, L. M. (2024a). Impact of feedback generation and presentation on self-monitoring behaviors, dietary intake, physical activity, and weight: A systematic review and meta-analysis. *International Journal of Behavioral Nutrition and Physical Activity, 21*, Article 3. https://doi.org/10.1186/s12966-023-01555-6

Krukowski, R. A., Denton, A. H., & König, L. M. (2024b). Correction: Impact of feedback generation and presentation on self-monitoring behaviors, dietary intake, physical activity, and weight: A systematic review and meta-analysis. *International Journal of Behavioral Nutrition and Physical Activity, 21*, Article 20. https://doi.org/10.1186/s12966-024-01569-8

Steinberg, D. M., Tate, D. F., Bennett, G. G., Ennett, S., Samuel-Hodge, C., & Ward, D. S. (2013). The efficacy of a daily self-weighing weight loss intervention using smart scales and e-mail. *Obesity, 21*(9), 1789-1797. https://doi.org/10.1002/oby.20396

Web Accessibility Initiative. (2025). *Understanding Success Criterion 1.4.1: Use of color*. World Wide Web Consortium. https://www.w3.org/WAI/WCAG22/Understanding/use-of-color.html

Web Accessibility Initiative. (2026). *Understanding Success Criterion 1.4.3: Contrast (minimum)*. World Wide Web Consortium. https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html

Withings. (n.d.). *Body+: Syncing my data*. https://support.withings.com/hc/en-us/articles/219050907-Body-Syncing-my-data
