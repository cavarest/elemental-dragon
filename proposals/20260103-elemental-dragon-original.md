# 20260103 - Elemental Dragon extensions

## Objective

Create a Minecraft Paper plugin that restricts players to possessing only one of
specific custom items in their inventory at a time. Players cannot hold
duplicate items or store them in bundles, chests, ender chests, shulkers,
barrels, etc.

## Custom Items

### Burning Fragment

#### General

* Model: Fire Charge (with enchanted glint)

* Craftable Quantity: 2

#### Crafting Recipe

(formatted in a graph that resembles the crafting table slots):

```
nt	st	nt
ni	hc	ni
nt	rt	nt
```

Items:
* nt = Netherite Upgrade Smithing Template
* st = Bolt Armor Trim
* ni = Netherite Ingot
* hc = Heavy Core
* rt = Rib Armor Trim

#### Abilities

* `/fire 1`
  * Summons a fireball that targets the closest entity in 10 ticks, dealing 3
    hearts of damage (no block destruction) and negating armor. Affects all
    players in a 5 block radius.
  * Cooldown: 2 minutes.

* `/fire 2`
  * Sets all players (except the wielder) in a 10 block radius on fire, dealing
    1 heart per second for up to 10 hearts.
  * Area marked by an orange circle visible to all players.
  * Cooldown: 3 minutes.

#### HUD Display for ability 1 and 2

- **Location:** Middle-left of the screen
- **During Cooldown:** Display remaining time in minutes(only once cooldown is
  below 1 minute) and seconds (e.g., “7 minutes, 59 seconds”, “7 minutes, 59
  seconds”, etc.)
- **When ability 1 Ready:** Display “Fireball fully charged”
- **When ability 2 Ready:** Display “Furnace ready”


#### Additional Considerations

- Handle edge cases: no valid targets etc.
- Make sure both messages for the 2 abilities of the agility core can both fit
  without overlapping and also be centered on the middle of the screen
- Ensure the fireball strikes are visual (orange particles/effects) and deal the
  specified damage
- Prevent ability spam by enforcing the cooldown properly


### Agility Fragment

#### General

* Model: Wind Charge (with enchanted glint)
* Craftable Quantity: 2

## Crafting Recipe

(formatted in a graph that resembles the crafting table slots)

```
br	ft	ad
bd	hc	bd
ad	ft	br
```

Items are:

* br = Breeze Rod
* ft = Flow Armor Trim
* ad = Ancient Derbis
* bd = Block of Diamond
* hc = Heavy Core

#### Abilities

* `/agile 1`:
  * Dashes the player 20 blocks in the direction they are looking within 1.5
    seconds. Negates damage while flying and landing.
  * Cooldown: 45 seconds.

* `/agile 2`:
  * Pushes all players in an 8 block radius 20 blocks away from the wielder
    within 2 seconds and applies slow falling for 10 seconds.
  * Cooldown: 2 minutes.


#### HUD Display for ability 1 and 2

- **Location:** Middle-left of the screen
- **During Cooldown:** Display remaining time in minutes(only once cooldown is
  below 1 minute) and seconds (e.g., “7 minutes, 59 seconds”, “7 minutes, 59
  seconds”, etc.)
- **When ability 1 Ready:** Display “Dash ready”
- **When ability 2 Ready:** Display “Continental explosion ready”

#### Additional Considerations

- Handle edge cases: no valid targets etc.
- Make sure both messages for the 2 abilities of the agility core can both fit
  without overlapping and also be centered on the middle of the screen
- Prevent ability spam by enforcing the cooldown properly


### Immortal Fragment

#### General

* Model: Totem of Undying (with enchanted glint)

* Craftable Quantity: 2

#### Crafting Recipe

(formatted in a graph that resembles the crafting table slots)::

```
tu	ad	tu
bd	hc	bd
tu	ad	tu
```

Items are:
* tu = Totem of Undying
* ad = Ancient Debris
* bd = Block of Diamond
* hc = Heavy Core

#### Abilities

* `/immortal 1`:
  * Provides a 1/5 chance to avoid damage for 15 seconds after activation (plays
    anvil sound on miss).
  * Cooldown: 2 minutes.

* `/immortal 2`:
  * Grants the wielder a second life if reduced to 0 hearts for 30 seconds
    post-activation, retaining previous effects (e.g., fire resistance).
  * Cooldown: 8 minutes.


#### HUD Display for ability 1 and 2

- **Location:** Middle-left of the screen
- **During Cooldown:** Display remaining time in minutes(only once cooldown is
  below 1 minute) and seconds (e.g., “7 minutes, 59 seconds”, “7 minutes, 58
  seconds”, etc.)
- **When ability 1 Ready:** Display “Instinctive weave ready”
- **When ability 2 Ready:** Display “2nd life ready”


#### Additional Considerations

- Handle edge cases: no valid targets etc.
- Make sure both messages for the 2 abilities of the agility core can both fit
  without overlapping and also be centered on the middle of the screen
- Prevent ability spam by enforcing the cooldown properly


### Corrupted Core

#### General

* Model: Heavy Core (with enchanted glint)
* Craftable Quantity: 1

#### Crafting Recipe

```
wr	hc	ws
bn	ns	bn
ws	hc	wr
```

Items are:
* wr = Wither Rose
* hc = Heavy Core
* ws = Wither Skeleton Skull
* bn = Block of Netherite
* ns = Nether Star


#### Abilities

* `/corrupt 1`:
  * Prevents the victim from acting (moving, eating, attacking, etc.) upon being
    hit.
  * Cooldown: 3 minutes.
* `/corrupt 2`:
  * Applies "life steal," healing the wielder half the damage dealt to any
    entity for 20 seconds.
  * Cooldown: 2 minutes.


#### HUD Display for ability 1 and 2

- **Location:** Middle-left of the screen
- **During Cooldown:** Display remaining time in minutes(only once cooldown is
  below 1 minute) and seconds (e.g., “7 minutes, 59 seconds”, “7 minutes, 59
  seconds”, etc.)
- **When ability 1 Ready:** Display “Corrupted freeze ready”
- **When ability 2 Ready:** Display “Heart stealer ready”


#### Additional Considerations

- Handle edge cases: no valid targets etc.
- Make sure both messages for the 2 abilities of the agility core can both fit
  without overlapping and also be centered on the middle of the screen
- Prevent ability spam by enforcing the cooldown properly



## Summary

Ensure the implementation of all four custom items, each with unique crafting
recipes and abilities, while enforcing inventory restrictions.
