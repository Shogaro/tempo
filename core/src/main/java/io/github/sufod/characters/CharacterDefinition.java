package io.github.sufod.characters;

public class CharacterDefinition {
    // Identifier used by UI or save data.
    public String id;
    // Display name shown in the selection screen.
    public String name;
    // Stat block used to build the Character instance.
    public CharacterStats stats;
    // Path to the animations JSON in assets.
    public String animations;
}
