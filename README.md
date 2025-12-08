# SAS Saab Security Database

This repository contains the hash database for **2,721,503** Saab vehicles, representing the final known shipped version of the Security Access Database.

## Overview

- **Total Records**: 2,721,503 VINs
- **Last Shipped Date**: March 1, 2011
- **Database Version**: 12EF (Model Year 2012)
- **Release Name**: MY12_SSA_20110228_0730

## Database Version Information

Based on internal metadata files:

### From RELEASE.csv
- **RELEASE_ID**: 2
- **RELEASE_DATE**: 2011-03-01 11:48:07.0
- **DESCRIPTION**: Security Access DB - ModelYear 2011 (MY12_SSA_20110228_0730)
- **VERSION**: 1

### From SECVERSION.csv
- **VERSION**: 12EF
- **FREESHOTS**: 16

## Critical Properties

- **Records**: 2,721,503 (plus 1 header line)
- **Size**: 213 MB (223,163,313 bytes)
- **MD5**: `f1d690a57b21c49b31889798e5e7b59d`
- **Version**: 12EF (Model Year 2012)
- **Release**: MY12_SSA_20110228_0730
- **Date**: March 1, 2011

## Database Schema

The main data is stored in `TSECCODE.csv`.

```sql
CREATE TABLE TSECCODE (
    CARLINE TEXT,      -- Model series (F, B, H, etc.)
    MODELYEAR TEXT,    -- Year code (3,4,5,6,7,8,9,A,B,C,etc.)
    PLANT TEXT,        -- Plant code (1,2,6,9)
    CHASSIS TEXT,      -- 6-digit chassis number
    GROUPID INTEGER,   -- Hardware group ID
    SECCODE_IMMO TEXT, -- 32-byte hex (encrypted immobilizer)
    SECCODE_INFO TEXT  -- 32-byte hex (encrypted infotainment)
);
```

### Sample Data
First record example:
```csv
F,3,1,000001,0,e981aa4559e67080a040a00020e02060,f8cf66f4643860e00020604060602060
```
