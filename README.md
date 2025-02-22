# GrassPathSpeedPlugin

A simple yet robust Minecraft Paper plugin that grants players a continuous **Speed II** effect while walking or jumping on Dirt Path (previously known as Grass Path). It ensures a seamless experience whether a player is:

- Walking/Running in a straight line on a Dirt Path  
- Jumping or briefly losing contact with the block  
- Transitioning between blocks with a short grace period

## Key Features

1. **Continuous Speed II**  
   - As long as the player remains (or quickly returns) on the Dirt Path block, they maintain a Speed II effect without abrupt interruptions.

2. **Grace Period**  
   - The plugin uses a short grace period (by default 1 second) so that small jumps, slight edge movements, or short block gaps won't cause the speed effect to flicker on and off.

3. **Combined Checks**  
   - Uses a dual check approach:
     - **Corner (Bounding Box) Check**: Verifies if the player is fully on a Dirt Path block (all four corners).  
     - **Single Block (Below) Check**: Allows for consistent speed effect when the player is jumping or only partially above the block.  
   - If either check detects the player is on/above Dirt Path, the speed effect is applied.

4. **Configurable Intervals**  
   - By default, the plugin checks every 5 ticks (0.25 seconds). This can be adjusted to balance performance and responsiveness for larger servers.

<img src="https://github.com/can61cebi/GrassPathSpeedPlugin/blob/main/image/demo.gif" width="400">

## Installation

1. **Download** the latest `.jar` file from [Releases]([./releases](https://github.com/can61cebi/GrassPathSpeedPlugin/releases/tag/Minecraft)).
2. **Place** the `.jar` into your Paper server's `plugins` folder.
3. **Start or restart** your Paper server.
4. The plugin should load automatically, and console logs will confirm successful initialization.

## Configuration

- **Grace Period**: The default is 1000 milliseconds (1 second).  
- **Effect Duration**: 60 ticks (3 seconds) per renewal, ensuring it never runs out as long as the player remains or quickly returns to the path.  
- **Check Interval**: 5 ticks (0.25 seconds).  

These values are currently hardcoded but can be adjusted in the code if necessary. A future update may introduce a `config.yml` for easier customization.

## Compatibility

- **Paper version**: Designed for Paper Minecraft 1.21.x (tested specifically with 1.21.4).  
- **Java version**: Requires at least Java 17.

## Contributing

Pull requests are welcome. For significant changes, please open an issue first to discuss what you would like to change.
