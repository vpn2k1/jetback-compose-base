# Expense Journal — Fintech Mobile UI/UX Case Study

## Product concept

Expense Journal is a premium finance companion for ultra-fast expense recording, seamless Google Sheets synchronization, budget tracking, and actionable financial insights. The experience is modern, minimal, and polished, with a tactile, premium feel inspired by Revolut, Apple Wallet, Notion, and Linear.

Key pillars:
- Ultra-fast entry: capture spending in two taps or less.
- Intelligent sync: Google Sheets updates automatically and securely.
- Budget clarity: simple goals, progress, and guardrails.
- Data insights: contextual charts, trends, and intelligent recommendations.

## Design goals

1. Minimal and premium: elegant white space, subtle glass surfaces, refined typography, and restrained color.
2. Speed-first interaction: entry-focused onboarding and the home screen optimized for quick writes.
3. Brand trust: calm financial palette with confident accent colors and clear affordances.
4. Consistency: shared design system tokens, component library, and light/dark mode parity.

---

## Design system

### Color palette

#### Primary tokens
- `surface-100`: #F7F9FC (light), #0F1726 (dark)
- `surface-200`: #E8EDF7 (light), #17233C (dark)
- `surface-300`: #CDD6E7 (light), #243556 (dark)
- `text-primary`: #111827 (light), #F8FAFC (dark)
- `text-secondary`: #4B5563 (light), #CBD5E1 (dark)
- `accent-positive`: #22C55E
- `accent-warning`: #F59E0B
- `accent-critical`: #EF4444
- `accent-action`: #6366F1
- `border`: rgba(15, 23, 42, 0.12) (light), rgba(255, 255, 255, 0.10) (dark)

#### Semantic colors
- `budget-safe`: #22C55E
- `budget-alert`: #F59E0B
- `budget-over`: #EF4444
- `insight-highlight`: #8B5CF6
- `sync-success`: #22C55E
- `sync-error`: #F97316

### Typography

- `display-lg`: 34 / 40 / Inter Bold
- `headline-md`: 24 / 32 / Inter SemiBold
- `title-sm`: 18 / 28 / Inter Medium
- `body-lg`: 16 / 24 / Inter Regular
- `body-sm`: 14 / 20 / Inter Regular
- `label-sm`: 12 / 18 / Inter Medium
- `caption`: 11 / 16 / Inter Regular

### Spacing scale

- `space-0`: 4dp
- `space-1`: 8dp
- `space-2`: 12dp
- `space-3`: 16dp
- `space-4`: 20dp
- `space-5`: 24dp
- `space-6`: 32dp
- `space-7`: 40dp
- `space-8`: 48dp

### Elevation

- `elevation-0`: flat background
- `elevation-1`: soft card surface with 16% black on light, 16% white on dark
- `elevation-2`: elevated sheet mimic with subtle blur and stronger shadow for quick add modal
- `glow`: accent glow behind CTA chip and active chart points

### Motion

- Duration: 160ms for quick transitions, 240ms for card enters
- Easing: `cubic-bezier(0.22, 1, 0.36, 1)` for springy interactions
- Use subtle fade + translateY for list reveals

### Iconography and imagery

- Outline icons for navigation and labels
- Filled accent icons for primary actions and status chips
- Glassy card background with minimal gradient overlays
- Category icons use monochrome glyphs on muted pill backgrounds

---

## User flow

### 1. Launch & home
- User opens Expense Journal
- Home surface displays current balance, today’s spend, budget progress, and quick-add CTA
- Primary entry actions: `Quick expense`, `Add income`, `Sync status`

### 2. Quick recording
- Tap `Quick expense`
- Type, select category, optionally attach note
- Confirm with green accent button
- Transaction appears immediately in the timeline and sync queue

### 3. Google Sheets sync
- Background sync runs automatically when connected
- Sync status chip shows `Up to date`, `Syncing`, or `Offline`
- A dedicated screen explains the sync path, data preview, and manual refresh

### 4. Budget check
- Home budget card displays weekly/monthly allocation, remaining spend, and trend
- When a budget threshold approaches, the app surfaces a warning panel with actionable guidance

### 5. Insights
- User navigates to Insights
- Chart cards reveal spend by category, weekly spend trend, and forecasted savings
- Swipe across cards to compare metrics and reveal smart suggestions

### 6. Expense detail & editing
- Select an expense to open detail view
- Edit amount, category, note, and sync labels
- Mark as reimbursable, split, or archive

---

## Component library

### 1. App shell
- `TopBar`: left icon, page title, right action menu
- `BottomNav`: 4 items `Home`, `Quick Add`, `Budget`, `Insights`
- `FloatingActionButton`: quick add with premium gradient fill

### 2. Cards
- `PrimarySummaryCard`: current balance, spend summary, savings goal
- `BudgetCard`: progress bar, remaining amount, warning chip
- `SyncStatusCard`: icon, status text, last sync time, manual refresh button
- `InsightCard`: chart preview, metric headline, microcopy

### 3. Inputs and controls
- `SearchInput`: rounded field with inset icon and quick suggestions
- `AmountField`: largest input with numeric keypad styling and secondary hint
- `CategoryPill`: selectable tags with active/inactive states and icons
- `SegmentedControl`: `Day` / `Week` / `Month` / `Quarter`
- `Switch`: rounded toggle for auto-sync and notifications

### 4. Transaction list
- `ExpenseListItem`: amount, category icon, merchant, timestamp, sync badge
- `SectionHeader`: sticky day separator with daily total
- `EmptyTimeline`: centered illustration, title, subtitle, action button

### 5. Charts and indicators
- `LineChartCard`: area fill plus dot emphasis on latest value
- `DonutChartCard`: category distribution with percentage labels
- `Sparkline`: small trend line with positive/negative shading
- `ProgressRing`: budget consumption as ring with numeric center

### 6. States
- `LoadingPlaceholder`: shimmer surfaces for home cards and list rows
- `ErrorState`: icon, headline, copy, and retry CTA
- `EmptyState`: illustration, supportive copy, primary action
- `SuccessToast`: transient confirmation for added expense or sync

---

## Mobile screens

### Screen 1: Home dashboard
- Header: `Expense Journal`, date pill, profile/avatar
- Summary row: `Available budget`, `Today`, `Month` totals
- Primary card: `Rapid record` CTA with amount and category preview
- Budget card: `Monthly budget` progress, `£1,860 remaining`, `72% used`
- Recent expenses list: today's transactions with small sync status badges
- Bottom nav: active `Home` icon

#### Example data
- Balance: £5,240
- Today: £62.40
- Month: £1,380.50
- Budget target: £2,400
- Recent expenses: `Cafe Nero` £8.20, `Uber` £15.50, `Metro` £2.80, `Groceries` £36.00

### Screen 2: Quick add modal
- Transparent blur sheet over home
- Large `Amount` input: `£ 12.50`
- Category chips: `Food`, `Transport`, `Work`, `Travel`
- Note field: `Lunch with Sarah`
- Buttons: `Save` and `Cancel`
- Smart suggestion row: last category, most-used tag, split tip

### Screen 3: Google Sheets sync center
- Sync status pill: `Sheets connected` with checkmark
- Preview card: `Expense Journal → Google Sheets` and last updated time
- Sheet details: `Sheet: Monthly ledger`, rows synced: `142`, next sync in `12s`
- Primary action: `Sync now`
- Secondary links: `Manage connection`, `View in Sheets`

### Screen 4: Budget overview
- Top summary: `You have £540 left this week`
- Budget row: `Groceries`, `Transport`, `Subscriptions`, each with progress bars
- Warning card: `You’ve spent 82% of your Social budget` with recommended cutbacks
- Controls: toggle weekly/monthly budget, add custom goal

### Screen 5: Insights hub
- Header: `Financial pulse`
- Primary KPI cards: `Avg daily spend`, `Expenses this week`, `Forecast vs budget`
- Trend chart: 4-week spend area chart with selected point details
- Category breakdown: donor ring with `Food 34%`, `Transport 18%`, `Work 14%`
- Smart insight strip: `You saved £84 on food compared to last week`

### Screen 6: Expense details
- Transaction row expanded: merchant, category, amount, date/time
- Notes: `Lunch at Miller’s, reimbursable`
- Actions: `Edit`, `Split`, `Mark reimbursable`, `Delete`
- Sync badge: `Synced 2 min ago`

---

## Empty and alternative states

### Empty home timeline
- Title: `No expenses yet today`
- Subtitle: `Add your first entry in under 2 taps and watch your budget come alive.`
- CTA: `Quick add expense`
- Visual: minimalist currency graph illustration with soft gradient

### Empty budget
- Title: `Create your first budget`
- Copy: `Set a weekly or monthly limit to track spend and stay on target.`
- CTA: `Start budgeting`
- Secondary note: `Budget insights arrive automatically once you log expenses.`

### No sync connection
- Title: `Sync paused`
- Copy: `Expense Journal is offline. Your entries will save locally and sync when Google Sheets reconnects.`
- CTA: `Retry sync`
- Support link: `Open sync settings`

### No insights yet
- Title: `More data, more clarity`
- Copy: `Record a few expenses and we’ll show your top categories, trends, and savings opportunities.`
- CTA: `Add first expense`

---

## Charts and insights

### 1. Monthly spend trend
- Type: filled area chart with highlighted final point
- Metrics: `£2,380 this month`, `+4.8% vs last month`
- Data points: Week 1 £512, Week 2 £580, Week 3 £620, Week 4 £668

### 2. Category distribution
- Type: donut chart with four categories
- Values:
  - Food 34% (£803)
  - Transport 18% (£423)
  - Subscriptions 14% (£330)
  - Work 10% (£241)
  - Other 24% (£584)

### 3. Budget burn gauge
- Type: progress ring with gradient sweep
- Values: `76% used`, `£1,820 of £2,400`
- Behavior: green until 70%, amber until 90%, red past 90%

### 4. Savings forecast
- Type: sparkline card
- Data: `Saved £420 this month`, projection to `£1,230` in 3 months
- Microcopy: `If you maintain your current pace, you’ll save £1,200 by October.`

### 5. Google Sheets sync health
- Type: status timeline with sync points
- Metrics: `Last sync 2m ago`, `5 pending rows`, `1 active connection`
- Visual cue: vertical dot path with success and warning states

---

## Light and dark mode

### Light mode
- Background: `surface-100`
- Cards: `white` with `surface-200` accents
- Text: `text-primary` and `text-secondary`
- Strong accent: `accent-action` on CTA and selected states
- Shadows: soft and warm, anchored to the surface

### Dark mode
- Background: `surface-100` (dark variant)
- Cards: `surface-200` (dark variant)
- Text: `white` and `text-secondary` `#CBD5E1`
- Accent: vivid `accent-action` with luminous ring on active chips
- Glass: blurred surfaces with gradient overlays, `rgba(255, 255, 255, 0.06)` highlights

### Token mapping
- `surface-100` -> light `#F7F9FC`, dark `#0F1726`
- `surface-200` -> light `#E8EDF7`, dark `#17233C`
- `text-primary` -> light `#111827`, dark `#F8FAFC`
- `text-secondary` -> light `#4B5563`, dark `#CBD5E1`
- `border` -> light `rgba(15, 23, 42, 0.12)`, dark `rgba(255, 255, 255, 0.10)`
- `card-surface` -> light `#FFFFFF`, dark `#1E2B44`
- `shadow` -> light `0 20px 60px rgba(15, 23, 42, 0.08)`, dark `0 20px 60px rgba(0, 0, 0, 0.40)`

---

## High-fidelity UI notes

- Use a wide safe margin on mobile sized screens so the content breathes.
- Keep onboarding minimal: only connect Google Sheets and confirm weekly budget goal.
- Use a single accent color for primary actions and a calm green for positive financial outcomes.
- Support quick actions directly from home: `+ Expense`, `Scan receipt`, `Sync now`.
- Ensure the quick add modal elevates above the dashboard with a soft blur and rounded corners.
- Use microcopy to establish trust: `Synced 12s ago`, `Auto-saving`, `Budget is on track`.

## Recommended page flow

1. Home dashboard
2. Quick add sheet
3. Expense details
4. Budget overview
5. Insights hub
6. Sync center

---

## Realistic example data

| Transaction | Category | Amount | Time | Notes | Sync |
|---|---|---|---|---|---|
| Cafe Nero | Food | £8.20 | 08:15 | `Cappuccino & snack` | ✅
| Uber | Transport | £15.50 | 09:02 | `Airport ride` | ✅
| Sainsbury's | Groceries | £36.00 | 12:10 | `Weekly groceries` | ✅
| Spotify | Subscriptions | £9.99 | 14:05 | `Premium` | ✅
| Flight ticket | Travel | £320.00 | 15:20 | `Business trip` | ⏳

### Budget examples
- Monthly budget: £2,400
- Weekly budget: £640
- Current status: `£540 remaining this week`, `76% used this month`
- Alerts: `Social budget 82%`, `Dining budget 91%`

### Insight examples
- `Average daily spend: £81`
- `Top category: Food (£803)`
- `Forecast: £2,530 this month (+5%)`
- `Savings opportunity: Move one meal per week to home cooking to save £45`

---

## Implementation guidance

- Start with a `Home` dashboard that uses a single strong CTA and a summary card layout.
- Build the `Quick Add` interaction as a modal sheet anchored from the bottom.
- Create a dedicated sync center that communicates trust and transparency.
- Make budget warnings contextual and actionable, never punitive.
- Surface insights only once enough data exists, with a gentle prompt before then.

## Outcome

Expense Journal is designed to feel modern, fast, and premium while being approachable for daily use. The minimalist UI, refined component set, and careful light/dark mode token mapping create a consistent fintech experience on mobile.
