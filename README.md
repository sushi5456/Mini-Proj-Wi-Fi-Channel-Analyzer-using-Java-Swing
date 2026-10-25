# Wi-Fi Channel Analyzer

## Overview
Wi-Fi Channel Analyzer is a Java-based desktop application built using Swing. It allows users to enter Wi-Fi network details, analyze channel interference, and identify a suitable Wi-Fi channel based on calculated interference scores.

## Features
- Add Wi-Fi networks using SSID, channel, and signal strength.
- Display network information in a table.
- Remove selected networks.
- Clear all network entries.
- Analyze Wi-Fi channels 1, 6, and 11.
- Recommend a channel based on interference scores.
- Classify interference as Low, Moderate, or High.
- Generate a channel analysis report.

## Technologies Used
- Java
- Java Swing
- Java AWT

## Requirements
- Java Development Kit (JDK)
- Java IDE or terminal

## Installation and Execution

1. Download or clone the project.
2. Open a terminal in the project directory.
3. Compile the Java source file:

```bash
javac WifiAnalyzerGUI.java
```

4. Run the application:

```bash
java WifiAnalyzerGUI
```

## How to Use
1. Enter the Wi-Fi network name (SSID).
2. Select the Wi-Fi channel.
3. Set the signal strength in dBm.
4. Click **Add Network** to add the network to the table.
5. Repeat to add additional networks.
6. Click **Analyze Networks** to calculate interference and obtain a recommendation.
7. Select a network and click **Remove Selected** to delete it.
8. Click **Clear All** to remove all network entries.

## Channel Analysis
The application evaluates channels 1, 6, and 11 and calculates an interference score based on channel distance and signal strength.

### Channel Distance Scoring

| Channel Distance | Score |
|---|---|
| Same channel | 3 × signal weight |
| Distance of 1 | 2 × signal weight |
| Distance of 2 | 1 × signal weight |
| Distance greater than 2 | 0 |

### Signal Strength Weight

| Signal Strength | Weight |
|---|---|
| -55 dBm or stronger | 3 |
| Weaker than -55 dBm and at least -70 dBm | 2 |
| Weaker than -70 dBm | 1 |

The channel with the lowest total interference score is recommended.

### Interference Classification
- **Low:** Score less than or equal to 3.
- **Moderate:** Score greater than 3 and less than or equal to 12.
- **High:** Score greater than 12.

## Project Structure

```text
Wi-Fi-Channel-Analyzer/
└── WifiAnalyzerGUI.java
```

## Limitations
- Wi-Fi network details must be entered manually.
- The application does not automatically scan nearby Wi-Fi networks.
- Interference scores are estimates based on the implemented scoring algorithm.
- Only channels 1, 6, and 11 are evaluated for recommendations.
- Network data is not permanently saved.

## Future Enhancements
- Integrate live Wi-Fi network scanning.
- Add graphical channel and signal-strength visualizations.
- Implement saving and loading of network data.
- Improve the channel recommendation algorithm.
- Add detailed analysis reports.

## License
No license is currently specified for this project.
