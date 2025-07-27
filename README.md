## 530-Editor

*Under development.*

Feel free to download and test new editor: ![](https://imgur.com/sB1YHSZ.png) [Download](https://mega.nz/file/WjJQwSCI#jfi8CZOyzS9c-iifL__A10J6qzNNac8cHrBic85n6l8)

[![License][License: AGPL v3]][license-url] [![License][Shield: Fork]][fork-url]

[License: AGPL v3]: https://img.shields.io/badge/License-AGPL%20v3-khaki.svg

[license-url]: https://www.gnu.org/licenses/agpl-3.0.en.html

[Shield: Fork]: https://img.shields.io/badge/repository-fork-tan

[fork-url]: https://gitlab.com/2009scape/tools/2009scape-item-definition-editor

***

### Information
Fork of [2009scape-item-definition-editor](https://gitlab.com/2009scape/tools/2009scape-item-definition-editor)

***

### Libraries / Dependencies
This project uses the following libraries:

- **rs-cache-library**: [GitHub Repository](https://github.com/Displee/rs-cache-library)
- The [names.dat](data/names.dat) file is sourced from a public [Rune-Server thread](https://rune-server.org/threads/634-cache-file-hash-names.705673/).  
  It is provided as-is, and all credit goes to the original contributors. If you are the rightful owner and believe this file should not be included, please contact us.

***

### How to Build

To build the project, you will need to have Gradle installed on your system. Follow these steps:

1. Clone the repository to your local machine:

   ```bash
   git clone https://gitlab.com/rs2-emu/530-editor.git
   cd 530-editor
   ```

2. If you do not have Gradle installed, download and install it from the [official site](https://gradle.org/install/).

3. Build the tool using Gradle:

   ```bash
   gradle build
   ```

   This will compile the project and create a `.jar` file in the `build/libs/` directory.

4. Run the tool:

   After building, navigate to the `build/libs/` folder and execute the following command:

   ```bash
   java -jar 530-editor.jar
   ```

***

### License
This project is licensed under **GNU Affero General Public License v3.0 (AGPL-3.0)**.  
Any modifications or derivatives of this project must also be released under the same open-source license when distributed.

```
Licensed under the GNU Affero General Public License (AGPL) v3.0.
You may obtain a copy of the License at:
https://www.gnu.org/licenses/agpl-3.0.html

2009scape Item Definition Editor
Copyright (C) 2022  2009Scape / Tools
```
