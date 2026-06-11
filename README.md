# Expense Journal

> Track money. Own your data.

Expense Journal is a modern personal finance application focused on ultra-fast expense recording, automatic Google Sheets synchronization, budget tracking, and financial insights.

Unlike traditional expense tracking applications, Expense Journal uses **Google Sheets as the primary data storage layer**, allowing users to fully own and control their financial data.

---

## Vision

Most expense tracking applications fail because they require too much effort to record transactions.

Expense Journal aims to reduce expense recording to a few seconds while still providing powerful reporting and budgeting features.

### Traditional Flow

```text
Open App
↓
Enter Amount
↓
Choose Category
↓
Select Date
↓
Add Notes
↓
Save
```

### Expense Journal Flow

```text
Open App
↓
Type:
"Coffee 45k"
↓
Save
```

Done in less than 3 seconds.

---

# Core Principles

## 1. Fast Input

The application should allow users to record expenses as quickly as possible.

Examples:

```text
Coffee 45k
```

```text
Lunch 120k
```

```text
Gas 300k
```

```text
Electricity 1.2m
```

The system automatically extracts:

* Amount
* Category
* Date
* Description

---

## 2. User Owns Their Data

All expense records are synchronized to the user's Google Sheets account.

Benefits:

* No vendor lock-in
* Easy export
* Easy backup
* Easy sharing
* Accessible anywhere

---

## 3. Offline First

The application works even without internet access.

Expenses are:

```text
Saved locally
↓
Queued for synchronization
↓
Automatically uploaded later
```

---

## 4. Automatic Reporting

Users should immediately understand:

* Where money goes
* Spending trends
* Budget status
* Financial habits

---

# Features

## Authentication

### Google Sign-In

* Sign in with Google
* Multiple account support
* Secure token storage
* Session persistence

---

## Expense Management

### Create Expense

Fields:

* Amount
* Category
* Note
* Date
* Payment Method
* Tags

### Edit Expense

Modify any existing expense.

### Delete Expense

Soft delete with recovery support.

### Duplicate Expense

Quickly create similar transactions.

---

## Quick Expense Input

Natural text input:

```text
Coffee 45k
```

↓

```json
{
  "amount": 45000,
  "category": "Food",
  "description": "Coffee"
}
```

Supported formats:

```text
50k
500k
1m
1.2m
1,200,000
```

---

## Categories

Default Categories:

* Food
* Transport
* Shopping
* Entertainment
* Bills
* Health
* Education
* Travel
* Other

Users can:

* Create category
* Edit category
* Delete category
* Reorder category

---

## Expense History

Features:

* Search
* Filter
* Sorting
* Infinite scrolling

Filters:

* Date
* Category
* Amount
* Payment Method

---

# Google Sheets Integration

Expense Journal automatically creates a spreadsheet:

```text
Expense Journal
```

Structure:

```text
Dashboard
Settings
2026-06
2026-07
2026-08
```

---

## Monthly Sheets

Example:

| ID | Date       | Amount | Category  | Note   |
| -- | ---------- | ------ | --------- | ------ |
| 1  | 2026-06-11 | 45000  | Food      | Coffee |
| 2  | 2026-06-11 | 120000 | Transport | Fuel   |

---

## Synchronization

### Status

```text
Pending
Syncing
Success
Failed
```

### Workflow

```text
Create Expense
↓
Save Local Database
↓
Add To Sync Queue
↓
Google Sheets Upload
↓
Mark Synced
```

### Conflict Resolution

Latest update wins based on:

```text
updatedAt
```

---

# Budget Management

Users can create:

## Global Budget

Example:

```text
12,000,000 VND / Month
```

---

## Category Budget

Example:

```text
Food
3,000,000 VND
```

```text
Transport
1,000,000 VND
```

---

## Budget Alerts

| Threshold | Status   |
| --------- | -------- |
| 80%       | Warning  |
| 90%       | Critical |
| 100%      | Exceeded |

---

# Reports

## Dashboard

Display:

* Today's Spending
* Weekly Spending
* Monthly Spending
* Yearly Spending

---

## Category Analysis

Example:

```text
Food
35%
2,300,000 VND
```

---

## Charts

Supported:

* Pie Chart
* Bar Chart
* Line Chart
* Donut Chart

---

## Spending Insights

Examples:

```text
You spent 850,000 VND on coffee this month.
```

```text
Friday is your highest spending day.
```

```text
Food represents 35% of total expenses.
```

---

# Future Features

## OCR Receipt Scanner

Capture a receipt and automatically extract:

* Merchant
* Amount
* Date

Create expense automatically.

---

## Voice Input

Example:

```text
I spent 180 thousand on dinner.
```

↓

```text
Amount: 180000
Category: Food
```

---

## AI Financial Insights

Examples:

```text
You spend more on weekends than weekdays.
```

```text
Your spending trend increased 20% this month.
```

```text
Projected spending at month end: 12,500,000 VND.
```

---

## Family Sharing

Shared budgets and expenses.

Roles:

* Owner
* Member

---

## Subscription Tracking

Track recurring payments:

* Netflix
* Spotify
* ChatGPT
* Internet
* Electricity

---

# Technical Architecture

## Frontend

* Kotlin Multiplatform
* Compose Multiplatform

Platforms:

* Android
* iOS
* Desktop
* Web

---

## Local Database

SQLDelight

Tables:

```text
expenses
categories
budgets
settings
sync_queue
```

---

## Architecture

```text
Presentation
↓
ViewModel
↓
Use Cases
↓
Repository
↓
Data Sources
```

---

## Cloud Services

Google APIs:

* Google Sign-In
* Google Drive API
* Google Sheets API

---

# Project Structure

```text
shared/
├── commonMain/
│   ├── data/
│   ├── domain/
│   ├── presentation/
│   ├── models/
│   └── ui/
│
├── androidMain/
├── iosMain/
├── desktopMain/
└── wasmJsMain/
```

---

# Roadmap

## Phase 1

Foundation

* Project setup
* Navigation
* Theme system
* SQLDelight
* Expense CRUD

---

## Phase 2

Google Ecosystem

* Google Sign-In
* Google Sheets Integration
* Sync Engine

---

## Phase 3

Reporting

* Dashboard
* Charts
* Analytics
* Budgets

---

## Phase 4

Automation

* Quick Input Parser
* Smart Category Suggestions
* Templates

---

## Phase 5

AI Features

* Financial Insights
* Forecasting
* Spending Analysis

---

## Phase 6

Premium Features

* OCR Receipt Scanner
* Voice Input
* Family Sharing
* Subscription Tracking

---

# Success Metrics

### Product

* Expense creation time < 3 seconds
* Sync success rate > 99%
* Crash-free sessions > 99.5%

### User

* Daily Active Users
* Monthly Active Users
* Retention Rate

---

# Mission

Build the fastest expense recording experience while giving users complete ownership of their financial data through Google Sheets integration.
