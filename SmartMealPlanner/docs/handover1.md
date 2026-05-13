# Project Handover Document — Handover 1

**Project:** Smart Meal Planner  
**Course:** Software Engineering SoSe 26 — Bauhaus-Universität Weimar  
**Team:** Manish Harish Kumar, Om Gajanan Badgujar, Aayush Sudhakar Raibole, Sakshi Ashok Palhade  
**Date:** Handover Session (Tuesday)

---

## 1. Purpose of this Document

This document records all ambiguities, incompleteness issues, and imprecisions found in the requirements provided to our group, and how each issue was resolved during the handover meeting with the previous group.

---

## 2. Requirements Review

### REQ1 — Browse Available Recipes

**Original:** "The system shall allow users to browse available recipes."

| Issue Type    | Issue Description | Resolution |
|---------------|-------------------|------------|
| Imprecision   | "Browse" is vague — does it mean a simple list, search functionality, or filtering? | Resolved in handover: browsing means a scrollable list with category-based filtering. Full-text search is a future enhancement only. |
| Incompleteness | No mention of what information to show per recipe in the list view. | Resolved: show recipe name only in the list; full details (ingredients, instructions, prep time) shown on selection. |

**Conclusion:** Two imprecision/incompleteness issues resolved.

---

### REQ2 — Assign Recipes to Days

**Original:** "The system shall allow users to assign recipes to specific days of the week."

| Issue Type    | Issue Description | Resolution |
|---------------|-------------------|------------|
| Ambiguity     | Can multiple recipes be assigned to a single day (e.g., breakfast + dinner)? Or only one per day? | Resolved: one recipe per day per the MVP scope. Multiple-meal-per-day support is a future enhancement. |
| Incompleteness | No mention of whether assignments should be persistent across sessions. | Resolved: assignments must be saved (SQLite used for persistence). |
| Imprecision   | "Specific days" — are all 7 days required, or only weekdays? | Resolved: all 7 days (Monday–Sunday) are required. |

**Conclusion:** Three issues resolved.

---

### REQ3 — Generate Grocery List

**Original:** "The system shall generate a combined grocery list based on selected meals."

| Issue Type    | Issue Description | Resolution |
|---------------|-------------------|------------|
| Ambiguity     | "Combined" — does this mean duplicates are merged (e.g., 2 recipes using garlic → one entry) or listed separately? | Resolved: ingredients with the same name and unit are merged and quantities are summed. |
| Incompleteness | No mention of when/how the list is generated. On demand, or automatically? | Resolved: on demand via a "Generate Grocery List" button. |
| Imprecision   | No mention of what format/output the list should take (printable, exportable, etc.). | Resolved: displayed in-app with a copy-to-clipboard export option. Printing is a future enhancement. |

**Conclusion:** Three issues resolved.

---

### REQ4 — Group Grocery Items by Category

**Original:** "The system shall group grocery items into categories such as vegetables and dairy."

| Issue Type    | Issue Description | Resolution |
|---------------|-------------------|------------|
| Imprecision   | "Such as" suggests the category list is not exhaustive — which categories exactly? | Resolved: five categories defined — Vegetables, Proteins, Dairy, Grains, Other. "Other" catches all unlisted items. |
| Incompleteness | No definition of how items are assigned to categories. Automatic or manual? | Resolved: each ingredient in the recipe dataset is pre-tagged with its category. No manual re-categorisation needed. |

**Conclusion:** Two issues resolved.

---

## 3. Non-Functional Requirements

### Performance

**Original:** "The system shall respond within 2 seconds for standard operations."

| Issue Type | Description | Resolution |
|------------|-------------|------------|
| Imprecision | "Standard operations" not defined. | Resolved: applies to recipe browsing, meal plan updates, and grocery list generation. Load time on startup (CSV + DB) is excluded if dataset is large. |

**Conclusion:** One issue resolved.

### Usability

**Original:** "The interface shall be simple and intuitive."

| Issue Type | Description | Resolution |
|------------|-------------|------------|
| Imprecision | Subjective — no measurable criteria. | No formal resolution: accepted as a design guideline. The UI uses tab-based navigation, labelled buttons, and visual feedback (status bar) to meet the spirit of this requirement. |

**Conclusion:** No formal resolution (no issue in the strict sense — acknowledged as a design goal).

### Reliability

**Original:** "The application shall operate without crashes under normal usage."

| Issue Type | Description | Resolution |
|------------|-------------|------------|
| No issue   | Clear and unambiguous. | No action required. |

### Maintainability

**Original:** "The system shall follow modular design principles."

| Issue Type | Description | Resolution |
|------------|-------------|------------|
| No issue   | Addressed by layered architecture (Model / Service / Data / UI). | No action required. |

---

## 4. Summary

| Requirement | Issues Found | Issues Resolved |
|-------------|-------------|-----------------|
| REQ1        | 2           | 2               |
| REQ2        | 3           | 3               |
| REQ3        | 3           | 3               |
| REQ4        | 2           | 2               |
| NFR – Performance | 1    | 1               |
| NFR – Usability   | 1    | 0 (design guideline) |
| NFR – Reliability | 0    | —               |
| NFR – Maintainability | 0 | —               |
| **Total**   | **12**      | **11**          |

All functional requirement issues were fully resolved in the handover meeting with the previous group. The one unresolved item (usability) is acknowledged as a non-measurable design guideline rather than a gap.
