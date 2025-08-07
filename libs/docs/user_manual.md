# User Manual v1.0

This guide provides instructions for using this tool.

---

## Tools

- [Region Transfer](#region-transfer)
- [Sprite Manager](#sprite-manager)
- [Interface Transfer](#interface-transfer)
- [Index Transfer](#index-transfer)
- [Region Dumper](#region-dumper)
- [Index Export](#index-export)

---

## Region Transfer

![Region Transfer](../images/2.png)

**RegionTransfer** enables transferring individual map regions between cache directories. It provides tools for calculating region identifiers, working with XTEA keys, and backing up caches.

### Key Features

- **Region ID:** Numeric identifier of the map region.
- **XTEA:** Comma-separated encryption key (e.g., `123,456,789,0` or `0,0,0,0`).
- **Enter Region:** Convert region filename (`lX_Y`) to Region ID.
- **Region Output:** Convert Region ID back to filename.
- **Select Source Cache:** Choose the cache to transfer from.
- **Transfer Region:** Copy a region to the active cache using provided XTEA keys.
- **Backup Cache:** Create a `.zip` archive of the selected cache directory.

### How to Use

1. **Select Source Cache**  
   Click **Select Source Cache** and choose the folder containing the source cache.

2. **Enter Region ID and XTEA Key**  
   Provide the Region ID and corresponding XTEA key for encryption/decryption.

3. **Optional: Convert Formats**

   - Use **Calculate Region** to convert Region ID → filename (`lX_Y`).
   - Use **Calculate Region ID** to convert filename → Region ID.

4. **Transfer Region**  
   Click **Transfer Region** to copy the region to the active cache.

5. **Backup Cache**  
   Click **Backup Cache** to generate a ZIP archive of the selected cache.

---

## Sprite Manager

![Sprite Manager](../images/6.png)

**SpriteManager** is a tool for managing sprite archives stored in the game cache. It allows you to view, replace, export, import, and back up sprite frames.

### Key Features

- **Archive List:** Shows all sprite archive IDs.
- **Frame List:** Displays frame indices for the selected archive.
- **Preview Panel:** Zoomable image preview of the selected frame.
- **Control Buttons:** Tools for frame and archive operations.

### How to Use

1. **Load Archives**  
   Archives are loaded automatically on startup.

2. **View Frames**  
   Select a frame to preview. Use `Ctrl + Mouse Wheel` to zoom (0.1x–5x).

3. **Replace a Frame**  
   Select a frame → click **Replace Frame** → choose a PNG image.

4. **Export Frames**

   - **Dump Frame:** Export the selected frame as a PNG.
   - **Dump All Frames:** Export all frames from the current archive.

5. **Export All Archives**  
   Click **Export All Archives** to dump every sprite archive and frame as PNGs.

6. **Add Frames & Archives**

   - **Add Frame:** Append a PNG frame to the selected archive.
   - **Add Archive:** Create a new archive with one PNG frame.

7. **Delete Frames & Archives**
   - **Delete Frame:** Remove a frame (if more than one exists).
   - **Delete Archive:** Remove the entire archive.

---

## Interface Transfer

![Interface Transfer](../images/3.png)

**InterfaceTransfer** manages interface archives (index 3) within the cache. It allows transferring, importing, exporting, and deleting interface entries.

### How to Use

1. **Select Source Cache**  
   Click **Select Source Cache** and choose the source cache folder.

2. **Remove Archive**  
   Enter or select the archive ID, then click **Remove Archive** to delete it.

3. **Transfer Archive**

   - Enter the archive ID to transfer.
   - Click **Transfer Archive** and specify a new archive ID or reuse the original.

4. **Import Archive (.dat)**  
   Click **Import Archive (.dat)**, choose a `.dat` file, and optionally enter a target archive ID.

5. **Export Archive (.dat)**  
   Enter the archive ID, click **Export Archive (.dat)**, and choose a save location.

---

## Index Transfer

![Index Transfer](../images/4.png)

**IndexTransfer** copies selected index files from one cache to another, with an optional rebuild of the target cache after transfer.

### How to Use

1. **Select Source Cache**  
   Click **Select Source Cache** and choose the source cache directory.

2. **Select Target Cache**  
   Click **Select Target Cache** and choose the destination directory.

3. **Enter Index IDs**  
   Provide index IDs separated by commas (e.g., `0,1,2,7`).

4. **Transfer Indices**  
   Click **Transfer** to copy the selected indices.

5. **Rebuild Cache (Optional)**  
   Choose whether to rebuild the destination cache after transfer.

---

## Region Dumper

![Region Dumper](../images/5.png)

**RegionDumper** extracts map and landscape data from a cache. It requires a JSON file with XTEA keys to decrypt the landscape files.

### How to Use

1. **Enter Region ID**  
   Input the numeric region ID to extract.

2. **Load XTEA Keys**  
   Click **Select XTEA JSON** and select a `.json` file with the correct keys.

3. **Set Output Directory**  
   Default path is `./data/export/maps/` — can be customized.

4. **Dump Region**  
   Click **Dump Region** to extract:
   - `region_<id>_tiles.dat`
   - `region_<id>_objects.dat`

---

## Index Export

![Index Export](../images/1.png)

**IndexExport** is a GUI tool for extracting specific indexes from a cache in either old or new formats.

### Key Features

- **Input Folder:** Select the cache to extract from.
- **Output Folder:** Choose where extracted files are saved.
- **Cache Format:** Choose `Empty`, `Old`, or `New`.
- **Index Selector:** Select one or more indexes via checkboxes.
- **Progress Display:** Shows current status and completion.

### How to Use

1. **Select Input Folder**  
   Click **Select input folder** and choose the source cache directory.

2. **Select Output Folder**  
   Click **Select output folder** to define the output location.

3. **Choose Cache Format**  
   Select the correct format depending on your cache type.

4. **Select Indexes**  
   Use checkboxes to mark the indexes you wish to extract.

5. **Start Extraction**  
   Click **Extract**. Progress will be shown on the status bar.

---
