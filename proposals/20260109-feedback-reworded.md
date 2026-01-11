Changes for all items:
* The "heavy core" used to craft the elemental dragon fragments is already in the game. Put that one in the crafting recipe of the custom ones instead of the renamed "netherite ingot". All the elemental dragon fragments should use the existing "heavy core" item for the center of the crafting recipe.
* Each items have 2 abilities, so should show 2 different cooldowns at the same time on the player’s screen. There is no problem here we have the sidebar already working.
* When each item is crafted, a message will be broadcast in chat. “{player-name} has crafted the {item-name}.” (word this according to the elemental dragon theme)
* Once the amount of elemental dragon fragments crafted exceed the limit, when a player tries to craft it, a message will show on their screen saying that they can’t craft it anymore because it has reached the crafting limit.
* When a elemental dragon fragment leaves your inventory, the player does not retain its abilities. The player will unquip the abilities.
* Create a command `/withdrawability` name of elemental dragon fragment will remove the ability from the player (if they have the abilities equipped), but the item is not removed from the player's inventory.
* All elemental dragon fragments cannot be put into any chests or container of sorts.
* All elemental dragon fragments cannot be burnt from lava and fire.
* For an operator when they give themselves an elemental dragon ability through `/{element-ability-name} equip` when they do not have the item, they are given the item as well in their inventory, then equipped with the ability. If they already have the item, then there is no need to give them another one, just equip it.

Fire Fragment:
* Both abilities MUST negate armor, it does not do it now.
* /fire 1 MUST BE ABLE TO destroy blocks with the fire ball, it does not do it now.
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


Immortal Fragment:
* Immortal Fragment MUST prevent you from dying.
* Basically acting like the totem of undying in minecraft.

Corrupted Core:
* SHOULD NOT give you night vision infinitely or give you infinite strength 2.
* /corrupt 1 is broken now, it does not freeze the player when you hit them.
* /corrupt 2 is broken now, it does not do the life steal ability when the wielder hits another entity for the duration of 15 seconds.
* `/corrupt 1`:
  * Prevents the victim from acting (moving, eating, attacking, etc.) upon being
    hit.
  * Cooldown: 3 minutes.
* `/corrupt 2`:
  * Applies "life steal," healing the wielder half the damage dealt to any
    entity for 20 seconds.
  * Cooldown: 2 minutes.

Agility Fragment:
* FIX THE ABILITIES so that it fulfills both abilities that it was supposed to do.
* /agile 1:
  * Dashes the player 20 blocks in the direction they are looking within 1.5 seconds. Negates damage while flying and landing.
  * Cooldown: 45 seconds.
* /agile 2:
  * Pushes all players in an 8 block radius 20 blocks away from the wielder within 2 seconds and applies slow falling to enemies for 10 seconds.
  * Cooldown: 2 minutes.


