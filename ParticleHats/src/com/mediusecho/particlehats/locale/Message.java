package com.mediusecho.particlehats.locale;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.mediusecho.particlehats.Core;
import com.mediusecho.particlehats.configuration.CustomConfig;
import com.mediusecho.particlehats.util.StringUtil;

public enum Message {
	
	UNKNOWN ("&cUnknown Message"),
	
	/**
	 * Commands
	 */
	
	// Misc
	COMMAND_ERROR_UNKNOWN        ("&cUnknown command, try &7/h help &cfor a list of commands"),
	COMMAND_ERROR_NO_PERMISSION  ("&cYou don't have permission to use this command"),
	COMMAND_ERROR_PLAYER_ONLY    ("&cYou must be a player to use this command, try &7/h help"),
	COMMAND_ERROR_ARGUMENTS      ("&cWrong number of arguments/n&c{1}"),
	COMMAND_ERROR_MENU_EXISTS    ("&7'&c{1}&7' already exists"),
	COMMAND_ERROR_UNKNOWN_PLAYER ("&7Unable to find '&c{1}&7'"),
	COMMAND_ERROR_OFFLINE_PLAYER ("&7'&c{1}&7' is offline"),
	
	// Main Command
	COMMAND_MAIN_DESCRIPTION ("Main Command"),
	COMMAND_MAIN_USAGE       ("/h"),
	
	// Reload
	COMMAND_RELOAD_DESCRIPTION ("Reloads the plugin"),
	COMMAND_RELOAD_USAGE       ("/h reload"),
	COMMAND_RELOAD_SUCCESS     ("&2ParticleHats reloaded"),
	
	// Clear Command
	COMMAND_CLEAR_DESCRIPTION        ("Removes all of the players active particless"),
	COMMAND_CLEAR_USAGE              ("/h clear"),
	COMMAND_CLEAR_SUCCESS            ("&2All particles cleared"),
	COMMAND_CLEAR_PLAYER_DESCRIPTION ("Removes all particles for the target player"),
	COMMAND_CLEAR_PLAYER_USAGE       ("/h clear <player>"),
	COMMAND_CLEAR_PLAYER_SUCCESS     ("&2All particles cleared for {1}"),
	
	// Create Command
	COMMAND_CREATE_DESCRIPTION ("Creates a new menu with the given name"),
	COMMAND_CREATE_USAGE       ("/h create <menu name>"),
	
	// Edit Command
	COMMAND_EDIT_DESCRIPTION ("Opens a menu in the editor"),
	COMMAND_EDIT_USAGE       ("/h edit <menu name>"),
	
	// Meta Command
	COMMAND_META_DESCRIPTION ("Lets the player change meta properties while editing a menu"),
	COMMAND_META_USAGE       ("/h meta <value>"),
	
	// Open Command
	COMMAND_OPEN_DESCRIPTION ("Opens a menu"),
	COMMAND_OPEN_USAGE       ("/h open <menu name>"),
	
	/**
	 * Particles
	 */
	PARTICLE_NONE_NAME                     ("&bNone"),
	PARTICLE_NONE_DESCRIPTION              ("Does Nothing"),
	PARTICLE_BARRIER_NAME                  ("&bBarrier"),
	PARTICLE_BARRIER_DESCRIPTION           ("Displayed by Barrier blocks"),
	PARTICLE_BLOCK_CRACK_NAME              ("&bBlock Crack"),
	PARTICLE_BLOCK_CRACK_DESCRIPTION       (""),
	PARTICLE_BLOCK_DUST_NAME               ("&bBlock Dust"),
	PARTICLE_BLOCK_DUST_DESCRIPTION        (""),
	PARTICLE_BUBBLE_COLUMN_UP_NAME         ("&bBubble Column Up"),
	PARTICLE_BUBBLE_COLUMN_UP_DESCRIPTION  (""),
	PARTICLE_BUBBLE_POP_NAME               ("&bBubble Pop"),
	PARTICLE_BUBBLE_POP_DESCRIPTION        (""),
	PARTICLE_CLOUD_NAME                    ("&bCloud"),
	PARTICLE_CLOUD_DESCRIPTION             (""),
	PARTICLE_CRIT_NAME                     ("&bCritical Hit"),
	PARTICLE_CRIT_DESCRIPTION              (""),
	PARTICLE_CRIT_MAGIC_NAME               ("&bMagic Critical Hit"),
	PARTICLE_CRIT_MAGIC_DESCRIPTION        (""),
	PARTICLE_CURRENT_DOWN_NAME             ("&bCurrent Down"),
	PARTICLE_CURRENT_DOWN_DESCRIPTION      (""),
	PARTICLE_DAMAGE_INDICATOR_NAME         ("&bDamage Indicator"),
	PARTICLE_DAMAGE_INDICATOR_DESCRIPTION  (""),
	PARTICLE_DRAGON_BREATH_NAME            ("&bDragons Breath"),
	PARTICLE_DRAGON_BREATH_DESCRIPTION     (""),
	PARTICLE_DRIP_LAVA_NAME                ("&bDripping Lava"),
	PARTICLE_DRIP_LAVA_DESCRIPTION         (""),
	PARTICLE_DRIP_WATER_NAME               ("&bDripping Water"),
	PARTICLE_DRIP_WATER_DESCRIPTION        (""),
	PARTICLE_DOLPHIN_NAME                  ("&bDolphins Grace"),
	PARTICLE_DOLPHIN_DESCRIPTION           (""),
	PARTICLE_ENCHANTMENT_TABLE_NAME        ("&bEnchantment Runes"),
	PARTICLE_ENCHANTMENT_TABLE_DESCRIPTION (""),
	PARTICLE_END_ROD_NAME                  ("&bEnder Rod"),
	PARTICLE_END_ROD_DESCRIPTION           (""),
	PARTICLE_EXPLOSION_HUGE_NAME           ("&bHuge Explosion"),
	PARTICLE_EXPLOSION_HUGE_DESCRIPTION    (""),
	PARTICLE_EXPLOSION_LARGE_NAME          ("&bLarge Explosion"),
	PARTICLE_EXPLOSION_LARGE_DESCRIPTION   (""),
	PARTICLE_EXPLOSION_NORMAL_NAME         ("&bNormal Explosion"),
	PARTICLE_EXPLOSION_NORMAL_DESCRIPTION  (""),
	PARTICLE_FALLING_DUST_NAME             ("&bFalling Dust"),
	PARTICLE_FALLING_DUST_DESCRIPTION      (""),
	PARTICLE_FIREWORKS_SPARK_NAME          ("&bFirework Sparks"),
	PARTICLE_FIREWORKS_SPARK_DESCRIPTION   (""),
	PARTICLE_FLAME_NAME                    ("&bFlame"),
	PARTICLE_FLAME_DESCRIPTION             (""),
	PARTICLE_HEART_NAME                    ("&bHearts"),
	PARTICLE_HEART_DESCRIPTION             (""),
	PARTICLE_ITEM_CRACK_NAME               ("&bItem Crack"),
	PARTICLE_ITEM_CRACK_DESCRIPTION        (""),
	PARTICLE_LAVA_NAME                     ("&bLava"),
	PARTICLE_LAVA_DESCRIPTION              (""),
	PARTICLE_MOB_APPEARANCE_NAME           ("&bMob Appearance"),
	PARTICLE_MOB_APPEARANCE_DESCRIPTION    (""),
	PARTICLE_NAUTILUS_NAME                 ("&bNautilus"),
	PARTICLE_NAUTILUS_DESCRIPTION          (""),
	PARTICLE_NOTE_NAME                     ("&bNoteblock Notes"),
	PARTICLE_NOTE_DESCRIPTION              (""),
	PARTICLE_PORTAL_NAME                   ("&bPortal"),
	PARTICLE_PORTAL_DESCRIPTION            (""),
	PARTICLE_REDSTONE_NAME                 ("&bRedstone Dust"),
	PARTICLE_REDSTONE_DESCRIPTION          (""),
	PARTICLE_SLIME_NAME                    ("&bSlime"),
	PARTICLE_SLIME_DESCRIPTION             (""),
	PARTICLE_SMOKE_LARGE_NAME              ("&bLarge Smoke"),
	PARTICLE_SMOKE_LARGE_DESCRIPTION       (""),
	PARTICLE_SMOKE_NORMAL_NAME             ("&bNormal Smoke"),
	PARTICLE_SMOKE_NORMAL_DESCRIPTION      (""),
	PARTICLE_SNOW_SHOVEL_NAME              ("&bSnow Shovel"),
	PARTICLE_SNOW_SHOVEL_DESCRIPTION       (""),
	PARTICLE_SNOWBALL_NAME                 ("&bSnowballs"),
	PARTICLE_SNOWBALL_DESCRIPTION          (""),
	PARTICLE_SPELL_NAME                    ("&bSpells"),
	PARTICLE_SPELL_DESCRIPTION             (""),
	PARTICLE_SPELL_INSTANT_NAME            ("&bInstant Spells"),
	PARTICLE_SPELL_INSTANT_DESCRIPTION     (""),
	PARTICLE_SPELL_MOB_NAME                ("&bMob Spells"),
	PARTICLE_SPELL_MOB_DESCRIPTION         (""),
	PARTICLE_SPELL_MOB_AMBIENT_NAME        ("&bAmbient Mod Spells"),
	PARTICLE_SPELL_MOB_AMBIENT_DESCRIPTION (""),
	PARTICLE_SPELL_WITCH_NAME              ("&bWitch Spell"),
	PARTICLE_SPELL_WITCH_DESCRIPTION       (""),
	PARTICLE_SPIT_NAME                     ("&bLlama Spit"),
	PARTICLE_SPIT_DESCRIPTION              (""),
	PARTICLE_SQUID_INK_NAME                ("&bSquid Ink"),
	PARTICLE_SQUID_INK_DESCRIPTION         (""),
	PARTICLE_SUSPENDED_NAME                ("&bSuspended"),
	PARTICLE_SUSPENDED_DESCRIPTION         (""),
	PARTICLE_SUSPENDED_DEPTH_NAME          ("&bSuspended Depth"),
	PARTICLE_SUSPENDED_DEPTH_DESCRIPTION   (""),
	PARTICLE_SWEEP_ATTACK_NAME             ("&bSweeping Attack"),
	PARTICLE_SWEEP_ATTACK_DESCRIPTION      (""),
	PARTICLE_TOTEM_NAME                    ("&bTotem of Undying"),
	PARTICLE_TOTEM_DESCRIPTION             (""),
	PARTICLE_TOWN_AURA_NAME                ("&bTown Aura"),
	PARTICLE_TOWN_AURA_DESCRIPTION         (""),
	PARTICLE_VILLAGER_ANGRY_NAME           ("&bAngry Villager"),
	PARTICLE_VILLAGER_ANGRY_DESCRIPTION    (""),
	PARTICLE_VILLAGER_HAPPY_NAME           ("&bHappy Villager"),
	PARTICLE_VILLAGER_HAPPY_DESCRIPTION    (""),
	PARTICLE_WATER_BUBBLE_NAME             ("&bWater Bubbles"),
	PARTICLE_WATER_BUBBLE_DESCRIPTION      (""),
	PARTICLE_WATER_DROP_NAME               ("&bWater Droplets"),
	PARTICLE_WATER_DROP_DESCRIPTION        (""),
	PARTICLE_WATER_SPLASH_NAME             ("&bWater Splash"),
	PARTICLE_WATER_SPLASH_DESCRIPTION      (""),
	PARTICLE_WATER_WAKE_NAME               ("&bWater Wake"),
	PARTICLE_WATER_WAKE_DESCRIPTION        (""),
	PARTICLE_ITEMSTACK_NAME                ("&bItem Stack"),
	PARTICLE_ITEMSTACK_DESCRIPTION         ("&8Drops items instead of particles/n/n&cWarning: Too many items spawning/n&cmay cause lag"),
	
	/**
	 * Location
	 */
	LOCATION_HEAD_NAME  ("Head"),
	LOCATION_WAIST_NAME ("Waist"),
	LOCATION_FEET_NAME  ("Feet"),
	
	/**
	 * Action
	 */
	ACTION_EQUIP_NAME                       ("&bEquip"),
	ACTION_EQUIP_DESCRIPTION                ("&8Equips this particle"),
	ACTION_TOGGLE_NAME                      ("&bToggle"),
	ACTION_TOGGLE_DESCRIPTION               ("&8Toggles a players active/n&8particles on/off"),
	ACTION_CLOSE_NAME                       ("&bClose"),
	ACTION_CLOSE_DESCRIPTION                ("&8Closes this menu"),
	ACTION_DUMMY_NAME                       ("&bDummy"),
	ACTION_DUMMY_DESCRIPTION                ("&8Does nothing. Can be used/n&8to show information"),
	ACTION_OVERRIDE_NAME                    ("&bOverride"),
	ACTION_OVERRIDE_DESCRIPTION             ("&8Equips this hat ignoring/n&8permissions"),
	ACTION_CLEAR_NAME                       ("&bClear"),
	ACTION_CLEAR_DESCRIPTION                ("&8Removes all active particles"),
	ACTION_COMMAND_NAME                     ("&bCommand"),
	ACTION_COMMAND_DESCRIPTION              ("&8Executes a command"),
	ACTION_OPEN_MENU_NAME                   ("&bOpen Menu"),
	ACTION_OPEN_MENU_DESCRIPTION            ("&8Opens a menu"),
	ACTION_OPEN_MENU_PERMISSION_NAME        ("&bOpen Menu with Permission"),
	ACTION_OPEN_MENU_PERMISSION_DESCRIPTION ("&8Opens a menu only if/n&8the player has permission"),
	ACTION_PURCHASE_CONFIRM_NAME            ("&bAccept Purchase"),
	ACTION_PURCHASE_CONFIRM__DESCRIPTION    ("&8Accepts a purchase/n/n&cShould only be used in a purchase menu"),
	ACTION_PURCHASE_DENY_NAME               ("&bCancel Purchase"),
	ACTION_PURCHASE_DENY_DESCRIPTION        ("&8Cancels a purchase/n/n&cShould only be used in a purchase menu"),
	ACTION_PURCHASE_ITEM_NAME               ("&bPurchase Item"),
	ACTION_PURCHASE_ITEM_DESCRIPTION        ("&8Replaces this item with whichever hat/n&8the player is trying to purchase/n/n&cShould only be used in a purchase menu"),
	ACTION_MIMIC_NAME                       ("&bMimic Left Click"),
	ACTION_MIMIC_DESCRIPTION                ("&8Copies the left click action"),
	ACTION_DEMO_NAME                        ("&bDemo"),
	ACTION_DEMO_DESCRIPTION                 ("&8Let players equip this particle/n&8for a set amount of time"),
	ACTION_ACTIVE_PARTICLES_NAME            ("&bActive Particles"),
	ACTION_ACTIVE_PARTICLES_DESCRIPTION     ("&8Opens a special menu where the/n&8player can manage their/n&8active particles"),
	
	/**
	 * Modes
	 */
	MODE_ACTIVE_NAME                ("Active"),
	MODE_ACTIVE_DESCRIPTION         ("&8Always displays particles"),
	MODE_WHEN_MOVING_NAME           ("When Moving"),
	MODE_WHEN_MOVING_DESCRIPTION    ("&8Only displays particles when/n&8the player is moving"),
	MODE_WHEN_AFK_NAME              ("When AFK"),
	MODE_WHEN_AFK_DESCRIPTION       ("&8Only displays particles when/n&8the player is marked as AFK"),
	MODE_WHEN_PEACEFUL_NAME         ("When Peaceful"),
	MODE_WHEN_PEACEFUL_DESCRIPTION  ("&8Only displays particles when/n&8the player is not attacking"),
	MODE_WHEN_GLIDING_NAME          ("When Gliding"),
	MODE_WHEN_GLIDING_DESCRIPTION   ("&8Only displays particles when/n&8the player is using an Elytra"),
	MODE_WHEN_SPRINTING_NAME        ("When Sprinting"),
	MODE_WHEN_SPRINTING_DESCRIPTION ("&8Only display particles while/n&8the player is running"),
	MODE_WHEN_SWIMMING_NAME         ("When Swimming"),
	MODE_WHEN_SWIMMING_DESCRIPTION  ("&8Only display particles while/n&8the player is swimming"),
	
	/**
	 * Types
	 */
	TYPE_NONE_NAME                  ("&cNone"),
	TYPE_NONE_DESCRIPTION           (""),
	TYPE_HALO_NAME                  ("&bHalo"),
	TYPE_HALO_DESCRIPTION           ("&8A ring of particles for the/n&8players head"),
	TYPE_TRAIL_NAME                 ("&bTrail"),
	TYPE_TRAIL_DESCRIPTION          ("&8Displays a small cloud of particles/n&8with random offsets"),
	TYPE_CAPE_NAME                  ("&bCape"),
	TYPE_CAPE_DESCRIPTION           (""),
	TYPE_WINGS_NAME                 ("&bWings"),
	TYPE_WINGS_DESCRIPTION          (""),
	TYPE_VORTEX_NAME                ("&bVortex"),
	TYPE_VORTEX_DESCRIPTION         ("&8A Swirling cone that gets/n&8larger at the bottom"),
	TYPE_ARCH_NAME                  ("&bArch"),
	TYPE_ARCH_DESCRIPTION           ("&8A half circle that sits/n&8above the players head"),
	TYPE_ATOM_NAME                  ("&bAtom"),
	TYPE_ATOM_DESCRIPTION           (""),
	TYPE_SPHERE_NAME                ("&bSphere"),
	TYPE_SPHERE_DESCRIPTION         (""),
	TYPE_CRYSTAL_NAME               ("&bCrystal"),
	TYPE_CRYSTAL_DESCRIPTION        ("&8A Plumbob, from the Sims"),
	TYPE_HELIX_NAME                 ("&bHelix"),
	TYPE_HELIX_DESCRIPTION          (""),
	TYPE_INVERSE_VORTEX_NAME        ("&bInverse Vortex"),
	TYPE_INVERSE_VORTEX_DESCRIPTION (""),
	TYPE_HOOP_NAME                  ("&bHoop"),
	TYPE_HOOP_DESCRIPTION           ("&82 Particles that spin around/n&8the player in a circle"),
	TYPE_SUSANOO_NAME               ("&bSusanoo"),
	TYPE_SUSANOO_DESCRIPTION        (""),
	TYPE_ANGEL_WINGS_NAME           ("&bAngel Wings"),
	TYPE_ANGEL_WINGS_DESCRIPTION    (""),
	TYPE_CREEPER_HAT_NAME           ("&bCreeper Hat"),
	TYPE_CREEPER_HAT_DESCRIPTION    ("&8A Creepers lovely face"),
	TYPE_CLEAN_TRAIL_NAME           ("&bSingle Particle"),
	TYPE_CLEAN_TRAIL_DESCRIPTION    ("&8A lonely particle, can be used/n&8to create complex designs"),
	TYPE_TORNADO_NAME               ("&bTornado"),
	TYPE_TORNADO_DESCRIPTION        (""),
	TYPE_CUSTOM_NAME                ("&bCustom"),
	TYPE_CUSTOM_DESCRIPTION         (""),
	
	
	/**
	 * Meta State
	 */
	META_HAT_NAME_COMMAND_USAGE                 ("&6Use &f/h meta <name> &6to rename"),
	META_HAT_NAME_DESCRIPTION                   ("Type the &eName &finto chat, or '&ccancel&f' to return"),
	META_HAT_LABEL_USAGE                        ("&6Use &f/h meta <label> &6to rename"),
	META_HAT_LABEL_DESCRIPTION                  ("Type the &eLabel &finto chat, or '&6cancel&f' to return"),
	META_HAT_COMMAND_USAGE                      ("&6Use &f/h meta <command> &6to rename"),
	META_HAT_COMMAND_DESCRIPTION                ("Type the &eCommand &7(without '/') &finto chat, or '&6cancel&f' to return"),
	META_HAT_DESCRIPTION_USAGE                  ("&6Use &f/h meta <description> &6to rename"),
	META_HAT_DESCRIPTION_DESCRIPTION            ("Type the &eDescription &finto chat, or '&6cancel&f' to return"),
	META_HAT_PEMISSION_USAGE                    ("&6Use &f/h meta <permission> &6to rename"),
	META_HAT_PEMISSION_DESCRIPTION              ("Type the &ePermission &finto chat, or '&6cancel&f' to return"),
	META_HAT_PERMISSION_DESCRIPTION_USAGE       ("&6Use &f/h meta <description> &6to rename"),
	META_HAT_PERMISSION_DESCRIPTION_DESCRIPTION ("Type the &eDescription &finto chat, or '&6cancel&f' to return"),
	META_HAT_PERMISSION_MESSAGE_USAGE           ("&6Use &f/h meta <permission message> &6to rename"),
	META_HAT_PERMISSION_MESSAGE_DESCRIPTION     ("Type the &eMessage &finto chat, or '&6cancel&f' to return"),
	META_HAT_EQUIP_MESSAGE_USAGE                ("&6Use &f/h meta <equip message> &6to rename"),
	META_HAT_EQUIP_MESSAGE_DESCRIPTION          ("Type the &eMessage &finto chat, or '&6cancel&f' to return"),
	META_HAT_TAG_USAGE                          ("&6Use &f/h meta <tag> &6to set"),
	META_HAT_TAG_DESCRIPTION                    ("Type the &eTag &finto chat, or '&6cancel&f' to return"),
	META_MENU_TITLE_USAGE                       ("&6Use &f/h meta <title> &6to rename"),
	META_MENU_TITLE_DESCRIPTION                 ("Type the &eTitle &finto chat, or '&6cancel&f' to return"),
	META_NEW_MENU_NAME_USAGE                    ("&6Use &f/h meta <menu name> &6to create a new menu"),
	META_NEW_MENU_NAME_DESCRIPTION              ("Type the &eName &finto chat, or '&6cancel&f' to return"),
	
	/**
	 * Tracking
	 */
	TRACK_NOTHING_NAME       ("Don't Follow Movement"),
	TRACK_HEAD_MOVEMENT_NAME ("Follow Head Movement"),
	TRACK_BODY_ROTATION_NAME ("Follow Body Rotation"),
	
	/**
	 * Animation
	 */
	ANIMATION_STATIC_NAME          ("&bStatic"),
	ANIMATION_STATIC_DESCRIPTION   ("&8Displays all particles at once"),
	ANIMATION_ANIMATED_NAME        ("&bAnimated"),
	ANIMATION_ANIMATED_DESCRIPTION ("&8Iterates over a types particles/n&8displaying them as an animation"),
	
	/**
	 * Display Mode
	 */
	DISPLAY_MODE_DISPLAY_IN_ORDER_NAME ("Pick icons in order"),
	DISPLAY_MODE_DISPLAY_RANDOMLY_NAME ("Pick icons randomly"),
	
	/**
	 * Menu Editor Properties
	 */
	
	// General
	EDITOR_MISC_MAIN_MENU     ("&6Main Menu"),
	EDITOR_MISC_GO_BACK       ("&6Back"),
	EDITOR_MISC_EQUIP         ("&bTry it on"),
	EDITOR_MISC_NEW_PARTICLE  ("&bNew Particle"),
	EDITOR_MISC_NEXT_PAGE     ("&3Next Page"),
	EDITOR_MISC_PREVIOUS_PAGE ("&3Previous Page"),
	EDITOR_MISC_EMPTY_MENU    ("&cEmpty"),
	
	// Particles
	EDITOR_PARTICLE_MISC_DESCRIPTION          ("/n&8Current:/n&8» &e{1}/n/n&3Left Click to Change Particle"),
	EDITOR_PARTICLE_RGB_COLOUR_DESCRIPTION    ("/n&8Current:/n&8» &e{1}/n/n&8Colour:/n&8» R: &e{2}/n&8» G: &e{3}/n&8» B: &e{4}/n/n&3Left Click to Change Particle/n&cRight Click to Change Colour"),
	EDITOR_PARTICLE_RANDOM_COLOUR_DESCRIPTION ("/n&8Current:/n&8» &e{1}/n/n&8Colour:/n&8» &eRandom/n/n&3Left Click to Change Particle/n&cRight Click to Change Colour"),
	EDITOR_PARTICLE_MISC_COLOUR_DESCRIPTION   ("/n&8Current:/n&8» &e{1}/n/n&3Left Click to Change Particle/n&cRight Click to Change Colour"),
	EDITOR_PARTICLE_BLOCK_DESCRIPTION         ("/n&8Current:/n&8» &e{1}/n/n&8Block:/n&8» &e{2}/n/n&3Left Click to Change Particle/n&cRight Click to Change Block Data"),
	EDITOR_PARTICLE_ITEM_DESCRIPTION          ("/n&8Current:/n&8» &e{1}/n/n&8Item:/n&8» &e{2}/n/n&3Left Click to Change Particle/n&cRight Click to Change Item Data"),
	EDITOR_PARTICLE_ITEMSTACK_DESCRIPTION     ("/n&8Current:/n&8» &e{1}/n/n&8Items:/n&8» &e{2}/n/n&3Left Click to Change Particle/n&cRight Click to Edit Items"),
	
	// Base Menu
	EDITOR_BASE_MENU_TITLE         ("Editing ({1=...})"),
	EDITOR_EMPTY_SLOT_TITLE        ("&bEmpty Slot"),
	EDITOR_SLOT_DESCRIPTION        ("&3Left Click to Edit/n&3Right Click for Settings"),
	EDITOR_HAT_GENERIC_DESCRIPTION ("&7Slot &f{1}/n&7Type: &f{2=Custom}/n&7Location: &f{3}/n&7Mode: &f{4}/n&7Update: &f{5} &7tick{6=s}"),
	EDITOR_HAT_COMMAND_DESCRIPTION (""),
	
	// Settings Menu
	EDITOR_SETTINGS_MENU_TITLE             ("Menu Settings"),
	EDITOR_SETTINGS_MENU_SET_TITLE         ("&bSet Menu Title"),
	EDITOR_SETTINGS_MENU_SET_SIZE          ("&bSet Menu Size"),
	EDITOR_SETTINGS_MENU_DELETE            ("&cDelete"),
	EDITOR_SETTINGS_MENU_SET_PURCHASE_MENU ("&bPurchase Menu"),
	EDITOR_SETTINGS_MENU_TOGGLE_LIVE_MENU  ("&bToggle Live Updates"),
	EDITOR_SETTINGS_MENU_SYNC_ICONS        ("&bSync Icons"),
	
	EDITOR_SETTINGS_MENU_TITLE_DESCRIPTION     ("&8Current Title: {1}"),
	EDITOR_SETTINGS_MENU_ANIMATION_DESCRIPTION ("/n&8Live Updates:/n&8» {1=&aEnabled}{2=&cDisabled}/n/n&8Hats will cycle through their/n&8icons and display them/n/n&3Click to Toggle"),
	EDITOR_SETTINGS_SYNC_DESCRIPTION           ("&8Resets each hat's animation index to 0/n&8so each hat is synced"), 
	
	// Delete Menu
	EDITOR_DELETE_MENU_TITLE ("Delete this Menu?"),
	EDITOR_DELETE_MENU_YES   ("&2Yes"),
	EDITOR_DELETE_MENU_NO    ("&cI've changed my mind"),
	
	// Resize Menu
	EDITOR_RESIZE_MENU_TITLE               ("Resize this menu"),
	EDITOR_RESIZE_MENU_SET_ROW_SIZE        ("&b{1} Row{2=s}"),
	EDITOR_RESIZE_MENU_SET_ROW_DESCRIPTION ("&cResizing this menu may result/n&cin lost particle data"),
	
	// Main Menu
	EDITOR_MAIN_MENU_TITLE                ("Main Menu"),
	EDITOR_MAIN_MENU_SET_TYPE             ("&bSet Type"),
	EDITOR_MAIN_MENU_SET_LOCATION         ("&bSet Location"),
	EDITOR_MAIN_MENU_SET_META             ("&bSet Meta Properties"),
	EDITOR_MAIN_MENU_SET_PRICE            ("&bSet Purchase Price"),
	EDITOR_MAIN_MENU_SET_SPEED            ("&bSet Speed"),
	EDITOR_MAIN_MENU_SET_MODE             ("&bSet Mode"),
	EDITOR_MAIN_MENU_SET_ACTION           ("&bSet Action"),
	EDITOR_MAIN_MENU_SET_UPDATE_FREQUENCY ("&bSet Update Frequency"),
	EDITOR_MAIN_MENU_SET_COUNT            ("&bSet Particle Count"),
	EDITOR_MAIN_MENU_SET_ANGLE            ("&bSet Angle"),
	EDITOR_MAIN_MENU_SET_OFFSET           ("&bSet Offset"),
	EDITOR_MAIN_MENU_SET_TRACKING_METHOD  ("&bSet Tracking Method"),
	EDITOR_MAIN_MENU_SET_SOUND            ("&bSet Sound"),
	EDITOR_MAIN_MENU_SET_POTION           ("&bSet Potion"),
	EDITOR_MAIN_MENU_SET_PARTICLE         ("&bSelect a new Particle"),
	EDITOR_MAIN_MENU_SET_ICON             ("&bSet Item"),
	EDITOR_MAIN_MENU_SET_SLOT             ("&bSet Slot"),
	EDITOR_MAIN_MENU_SET_SCALE            ("&bSet Scale"),
	EDITOR_MAIN_MENU_CLONE                ("&bCreate a Copy"),
	EDITOR_MAIN_MENU_MOVE                 ("&bMove"),
	EDITOR_MAIN_MENU_EDIT_PARTICLES       ("&bEdit Particles"),
	EDITOR_MAIN_MENU_EDIT_NODES           ("&bEdit Nodes"),
	EDITOR_MAIN_MENU_NO_PARTICLES         ("&bNo Particles"),
	EDITOR_MAIN_MENU_CUSTOM_TYPE_ERROR    ("&cUnable to find image"),
	
	EDITOR_MAIN_MENU_COUNT_DESCRIPTION                    ("/n&8» &e{1}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1"),
	EDITOR_MAIN_MENU_SPEED_DESCRIPTION                    ("/n&8» &e{1}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1"),
	EDITOR_MAIN_MENU_PRICE_DESCRIPTION                    ("/n&8» &e{1=Free} &8{2}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1/n&cShift Click to Adjust by 10"),
	EDITOR_MAIN_MENU_VECTOR_DESCRIPTION                   ("/n&8» X: &e{1}/n&8» Y: &e{2}/n&8» Z: &e{3}/n/n&3Left Click to Change/n&cShift Right Click to Clear"),
	EDITOR_MAIN_MENU_LOCATION_DESCRIPTION                 ("/n&8• {1}/n&8» &e{2}/n&8• {3}/n/n&3Left Click to Cycle Down/n&3Right Click to Cycle Up"),
	EDITOR_MAIN_MENU_MODE_DESCRIPTION                     ("/n&8• {1}/n&8» &e{2}/n&8• {3}/n/n{4}/n/n&3Left Click to Cycle Down/n&3Right Click to Cycle Up"),
	EDITOR_MAIN_MENU_UPDATE_FREQUENCY_DESCRIPTION         ("/n&7» &8Updates every &e{1} &8tick{2=s}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1"),
	EDITOR_MAIN_MENU_ICON_DESCRIPTION                     ("&8Change the item that will we displayed/n&8inside this menu"),
	EDITOR_MAIN_MENU_SOUND_DESCRIPTION                    ("/n&8Sound: &7{1=&cNot Set}/n&8Volume: &7{2}/n&8Pitch: &7{3}/n/n&3Left Click to Change{4=/n&cShift Right Click to Clear}"),
	EDITOR_MAIN_MENU_SLOT_DESCRIPTION                     ("&8Change where this hat will be/n&8inside this menu"),
	EDITOR_MAIN_MENU_SCALE_DESCRIPTION                    ("/n&8» &e{1}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&3Shift Click to Adjust by 1/n&cMiddle Click to Reset"),
	EDITOR_MAIN_MENU_CLONE_DESCRIPTION                    ("&8Create a copy of this hat/n&8and place it in a new slot"),
	EDITOR_MAIN_MENU_MOVE_DESCRIPTION                     ("&8Move this hat to a different menu"),
	EDITOR_MAIN_MENU_ACTION_DESCRIPTION                   ("/n&8Left Click Action:/n{1}/n/n&8Right Click Action:/n{2}/n/n&3Click to Change Actions"),
	EDITOR_MAIN_MENU_META_DESCRIPTION                     ("&8Edit various meta properties/n/n&3Left Click to Open/n&3Right Click for Description Shortcut"),
	EDITOR_MAIN_MENU_TYPE_DESCRIPTION                     ("/n&8Current:/n&8» &e{1=/n&8» }{2}/n/n&3Left Click to Change Type/n{3=&cShift Click to Change Animation}"),
	EDITOR_MAIN_MENU_ANIMATION_DESCRIPTION                ("/n/n&8Animation:/n&8» &e{1}/n{2}"),
	EDITOR_MAIN_MENU_TRACKING_METHOD_DESCRIPTION_SINGLE   ("&8» &e{1}"),
	EDITOR_MAIN_MENU_TRACKING_METHOD_DESCRIPTION_MULTIPLE ("/n&8• {1}/n&8» &e{2}/n&8• {3}/n/n&3Left Click to Cycle Down/n&3Right Click to Cycle Up"),
	EDITOR_MAIN_MENU_NODE_DESCRIPTION                     ("&8Add a new hat,/n&8or edit an existing one"),
	
	EDITOR_MAIN_MENU_NO_PARTICLES_DESCRIPTION             ("&8Select a Type that supports particles"),
	
	// Type Menu
	EDITOR_TYPE_MENU_TITLE                   ("Select a Type {1}/{2}"),
	EDITOR_TYPE_MENU_TYPE_PREFIX             ("&e"),
	EDITOR_TYPE_MENU_TYPE_DESCRIPTION        ("{1=/n/n}&8Supports &3{2} &8Particle{3=s}/n/n{4=&3Click to Select}{5=&3Selected}"),
	EDITOR_TYPE_MENU_CUSTOM_TYPE_DESCRIPTION ("{1=&3Click to Select}{2=&3Selected}"),
	EDITOR_TYPE_MENU_INCLUDED_FILTER         ("&bIncluded Types"),
	EDITOR_TYPE_MENU_CUSTOM_FILTER           ("&bCustom Types"),
	EDITOR_TYPE_MENU_NO_CUSTOM_TYPES         ("&cNo Custom Types Found"),
	
	// Potion Menu
	EDITOR_POTION_MENU_TITLE        ("Select a Potion {1}/{2}"),
	EDITOR_POTION_MENU_POTION_TITLE ("&b{1}"),
	EDITOR_POTION_MENU_SET_STRENGTH ("&bSet Strength"),
	
	// Move Menu
	EDITOR_MOVE_MENU_TITLE                ("Move to ({1=...})"),
	EDITOR_MOVE_MENU_MOVE                 ("&bMove Here"),
	EDITOR_MOVE_MENU_OCCUPIED             ("&cOccupied"),
	EDITOR_MOVE_MENU_MOVE_DESCRIPTION     ("&3Left Click to Move/n&cRight Click to Cancel"),
	EDITOR_MOVE_MENU_OCCUPIED_DESCRIPTION ("&cRight Click to Cancel"),
	
	// Node Overview Menu
	EDITOR_NODE_OVERVIEW_MENU_TITLE            ("Edit or Add a Node"),
	EDITOR_NODE_OVERVIEW_NODE_TITLE            ("&bNode #{1}"),
	EDITOR_NODE_OVERVIEW_MENU_NODE_DESCRIPTION ("&3Left Click to Edit/n&cShift Right Click to Delete"),
	EDITOR_NODE_OVERVIEW_MENU_ADD_NODE         ("&bCreate a new Node"),
	
	// Icon Menu
	EDITOR_ICON_OVERVIEW_MENU_TITLE               ("Add or Remove Items"),
	EDITOR_ICON_MENU_SET_MAIN_ICON                ("&bSet Main Item"),
	EDITOR_ICON_MENU_ADD_ICON                     ("&bAdd an Item"),
	EDITOR_ICON_MENU_PREVIEW                      ("&bPreview"),
	EDITOR_ICON_MENU_SET_DISPLAY_MODE             ("&bSet Display Mode"),
	EDITOR_ICON_MENU_SET_UPDATE_FREQUENCY         ("&bSet Update Frequency"),
	EDITOR_ICON_MENU_ITEM_PREFIX                  ("&b"),
	
	EDITOR_ICON_MENU_ITEM_TITLE                   ("Select an Item"),
	EDITOR_ICON_MENU_ITEM_INFO                    ("&bSelect an Item"),
	EDITOR_ICON_MENU_ITEM_DESCRIPTION             ("&8Select an Item from your inventory"),
	EDITOR_ICON_MENU_BLOCK_TITLE                  ("Select a Block"),
	EDITOR_ICON_MENU_BLOCK_INFO                   ("&bSelect a Block"),
	EDITOR_ICON_MENU_BLOCK_DESCRIPTION            ("&8Select a Block from your inventory"),
	
	EDITOR_ICON_MENU_UPDATE_FREQUENCY_DESCRIPTION ("/n&7» &8Updates every &e{1} &8tick{2=s}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1"),
	EDITOR_ICON_MENU_DISPLAY_MODE_DESCRIPTION     ("/n&8• {1}/n&7» &e{2}/n/n&3Left Click to Cycle Down/n&3Right Click to Cycle Up"),
	EDITOR_ICON_MENU_ICON_DESCRIPTION             ("&3Left Click to Change/n&cShift Right Click to Delete"),
	
	// Offset Menu
	EDITOR_OFFSET_MENU_TITLE                ("Set Offset"),
	EDITOR_OFFSET_MENU_SET_OFFSET_X         ("&bSet X Offset"),
	EDITOR_OFFSET_MENU_SET_OFFSET_Y         ("&bSet Y Offset"),
	EDITOR_OFFSET_MENU_SET_OFFSET_Z         ("&bSet Z Offset"),
	EDITOR_OFFSET_MENU_OFFSET_X_DESCRIPTION ("/n&e» X: {1}/n&8• Y: &7{2}/n&8• Z: &7{3}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&3Shift Click to Adjust by 1/n&cMiddle Click to Reset"),
	EDITOR_OFFSET_MENU_OFFSET_Y_DESCRIPTION ("/n&8• X: &7{1}/n&e» Y: {2}/n&8• Z: &7{3}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&3Shift Click to Adjust by 1/n&cMiddle Click to Reset"),
	EDITOR_OFFSET_MENU_OFFSET_Z_DESCRIPTION ("/n&8• X: &7{1}/n&8• Y: &7{2}/n&e» Z: {3}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&3Shift Click to Adjust by 1/n&cMiddle Click to Reset"),

	// Angle Menu
	EDITOR_ANGLE_MENU_TITLE               ("Set Angle"),
	EDITOR_ANGLE_MENU_SET_ANGLE_X         ("&bSet X Angle"),
	EDITOR_ANGLE_MENU_SET_ANGLE_Y         ("&bSet Y Angle"),
	EDITOR_ANGLE_MENU_SET_ANGLE_Z         ("&bSet Z Angle"),
	EDITOR_ANGLE_MENU_ANGLE_X_DESCRIPTION ("/n&e» X: {1}/n&8• Y: &7{2}/n&8• Z: &7{3}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&3Shift Click to Adjust by 1/n&cMiddle Click to Reset"),
	EDITOR_ANGLE_MENU_ANGLE_Y_DESCRIPTION ("/n&8• X: &7{1}/n&e» Y: {2}/n&8• Z: &7{3}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&3Shift Click to Adjust by 1/n&cMiddle Click to Reset"),
	EDITOR_ANGLE_MENU_ANGLE_Z_DESCRIPTION ("/n&8• X: &7{1}/n&8• Y: &7{2}/n&e» Z: {3}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&3Shift Click to Adjust by 1/n&cMiddle Click to Reset"),

	// Particle Selection Menu
	EDITOR_PARTICLE_MENU_TITLE               ("Supported Particles {1}/{2}"),
	EDITOR_PARTICLE_MENU_COLOUR_FILTER_TITLE ("Filter: Colour"),
	EDITOR_PARTICLE_MENU_DATA_FILTER_TITLE   ("Filter: Data"),
	EDITOR_PARTICLE_MENU_RECENT_FILTER_TITLE ("Recent Particles"),
	EDITOR_PARTICLE_MENU_COLOUR_FILTER       ("&3Colour Filter"),
	EDITOR_PARTICLE_MENU_DATA_FILTER         ("&3Data Filter"),
	EDITOR_PARTICLE_MENU_NORMAL_FILTER       ("&3Show All Particles"),
	EDITOR_PARTICLE_MENU_RECENT_FILTER       ("&3Recently Used"),
	
	// Particle Overview Menu
	EDITOR_PARTICLE_OVERVIEW_MENU_TITLE ("Edit Particles"),
	EDITOR_PARTICLE_OVERVIEW_PARTICLE_NAME ("&bParticle #{1}"),
	
	// Sound Menu
	EDITOR_SOUND_MENU_MISC_TITLE         ("Misc Sounds {1}/{2}"),
	EDITOR_SOUND_MENU_BLOCK_TITLE        ("Block Sounds {1}/{2}"),
	EDITOR_SOUND_MENU_ENTITY_TITLE       ("Entity Sounds {1}/{2}"),
	EDITOR_SOUND_MENU_SET_PITCH          ("&bSet Pitch"),
	EDITOR_SOUND_MENU_SET_VOLUME         ("&bSet Volume"),
	EDITOR_SOUND_MENU_BLOCK_FILTER       ("&bBlock Sound Filter"),
	EDITOR_SOUND_MENU_ENTITY_FILTER      ("&bEntity Sound Filter"),
	EDITOR_SOUND_MENU_MISC_FILTER        ("&bMisc Filter"),
	EDITOR_SOUND_MENU_SOUND_PREFIX       ("&b{1}"),
	EDITOR_SOUND_MENU_SOUND_DESCRIPTION  ("&3Left Click to Select/n&3Right Click to Hear{1=/n/n&8Selected}"),
	EDITOR_SOUND_MENU_VOLUME_DESCRIPTION ("/n&8» &e{1}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&cShift Click to Adjust by 1"),
	EDITOR_SOUND_MENU_PITCH_DESCRIPTION  ("/n&8» &e{1}/n/n&3Left Click to Add 0.1/n&3Right Click to Subtract 0.1/n&cShift Click to Adjust by 1"),
	
	// Action Menu
	EDITOR_ACTION_OVERVIEW_MENU_TITlE              ("Set Left/Right Click Actions"),
	EDITOR_ACTION_OVERVIEW_MENU_SET_LEFT_CLICK     ("&bSet Left Click Action"),
	EDITOR_ACTION_OVERVIEW_MENU_SET_RIGHT_CLICK    ("&bSet Right Click Action"),
	EDITOR_ACTION_OVERVIEW_MENU_ACTION_DESCRIPTION ("/n&8Current:/n{1}/n/n&3Left Click to Change Action{2=/n&cRight Click to Change Argument}"),
	EDITOR_ACTION_MENU_TITLE                       ("Select a {1} Action {2}/{3}"),
	EDITOR_ACTION_MENU_MISC_DESCRIPTION            ("&8» &e{1}"),
	EDITOR_ACTION_MENU_MENU_DESCRIPTION            ("&8» &e{1}/n&8» &7{2=&cNot Set}"),
	EDITOR_ACTION_MENU_COMMAND_DESCRIPTION         ("&8» &e{1}/n&8» &7{2=&cNot Set}"),
	EDITOR_ACTION_MENU_DEMO_DESCRIPTION            ("&8» &e{1}/n&8» &7{2}"),
	EDITOR_ACTION_MENU_ACTION_DESCRIPTION          ("{1}{2=/n/n&3Selected}{3=/n/n&3Click to Select}"),
	
	// Slot Menu
	EDITOR_SLOT_MENU_TITlE    ("Choose a new Slot"),
	EDITOR_SLOT_MENU_OCCUPIED ("&cOccupied"),
	EDITOR_SLOT_MENU_SWAP     ("&bSwap Places"),
	EDITOR_SLOT_MENU_CANCEL   ("&6Cancel"),
	EDITOR_SLOT_MENU_SELECT   ("&bSelect Slot"),
	
	// Duration Menu
	EDITOR_DURATION_MENU_TITLE        ("Set Duration"),
	EDITOR_DURATION_MENU_SET_DURATION ("&3Set Duration"),
	EDITOR_DURATION_MENU_DESCRIPTION  ("/n&8» &e{1}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1/n&cShift Click to Adjust by 30"),
	
	// Meta Menu
	EDITOR_META_MENU_TITLE                      ("Meta Properties"),
	EDITOR_META_MENU_SET_NAME                   ("&bChange Display Name"),
	EDITOR_META_MENU_SET_LABEL                  ("&bChange Label"),
	EDITOR_META_MENU_SET_PERMISSION             ("&bChange Permission"),
	EDITOR_META_MENU_SET_DESCRIPTION            ("&bChange Description"),
	EDITOR_META_MENU_SET_PERMISSION_MESSAGE     ("&bChange Permission Message"),
	EDITOR_META_MENU_SET_PERMISSION_DESCRIPTION ("&bChange Permission Description"),
	EDITOR_META_MENU_SET_EQUIP_MESSAGE          ("&bChange Equip Message"),
	EDITOR_META_MENU_SET_TAG                    ("&bChange Tags"),
	
	EDITOR_META_MENU_NAME_DESCRIPTION              ("/n&8Current:/n&8» {1}/n/n&3Left Click to Change{2=/n&cShift Right Click to Reset}"),
	EDITOR_META_MENU_LABEL_DESCRIPTION             ("/n&8Labels allow you to use this hat/n&8in commands like: &7/h set <label>/n/n&8Current:/n&8» &7{1=&cNot Set}/n/n&3Left Click to Change{2=/n&cShift Right Click to Clear}"),
	EDITOR_META_MENU_DESCRIPTION_DESCRIPTION       ("/n&8Current:/n{1}/n&3Left Click to Change{2=/n&cShift Right Click to Clear}"),
	EDITOR_META_MENU_EMPTY_DESCRIPTION             ("/n&8Current:/n&8» &cNot Set/n/n&3Left Click to Change"),
	EDITOR_META_MENU_PERMISSION_DESCRIPTION        ("/n&8Current:/n&8» &7{1}/n/n&8Usage:/n&8» &7{2}/n/n&3Click to Change"),
	EDITOR_META_MENU_EQUIP_DESCRIPTION             ("/n&8Show the player this message when/n&8they equip this hat instead of the/n&8global equip message/n/n&8Current:/n&8» &7{1=&cNot Set}/n/n&3Left Click to Change{2=/n&cShift Right Click to Clear}"),
	EDITOR_META_MENU_PERMISSION_DENIED_DESCRIPTION ("/n&8Current:/n&8» &7{1=&cNot Set}/n/n&3Left Click to Change{2=/n&cShift Right Click to Clear}"),
	
	// Description Menu
	EDITOR_DESCRIPTION_MENU_TITLE    ("Edit Description"),
	EDITOR_DESCRIPTION_MENU_ADD_LINE ("&bAdd a Line"),
	EDITOR_DESCIPRION_LINE_TITLE     ("&bLine #{1}"),
	EDITOR_DESCRIPTION_MENU_PREVIEW  ("&bPreview"),
	EDITOR_DESCRIPTION_MENU_EMPTY    ("&cNo Description Set"),
	
	EDITOR_DESCRIPTION_MENU_LINE_DESCRIPTION    ("/n&8» &r{1=&cEmpty}/n/n&3Left Click to Edit/n&cShift Right Click to Delete"),
	EDITOR_DESCRIPTION_MENU_PREVIEW_DESCRIPTION ("{1=&cEmpty}{2=/n&cShift Right Click to Clear}"),
	
	// ItemStack menu
	EDITOR_ITEMSTACK_MENU_TITLE          ("Add or Remove Items"),
	EDITOR_ITEMSTACK_MENU_ADD_ITEM       ("&bAdd an Item"),
	EDITOR_ITEMSTACK_MENU_SET_DURATION   ("&bSet Duration"),
	EDITOR_ITEMSTACK_MENU_TOGGLE_GRAVITY ("&bToggle Gravity"),
	EDITOR_ITEMSTACK_MENU_SET_VELOCITY   ("&bSet Velocity"),
	
	EDITOR_ITEMSTACK_MENU_DURATION_DESCRIPTION ("/n&8Duration:/n&8» &e{1}/n/n&8Set how long items stay/n&8spawned in the world/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1/n&cShift Click to Adjust by 10"),
	EDITOR_ITEMSTACK_MENU_GRAVITY_DESCRIPTION  ("/n&8Gravity:/n&8» {1=&aEnabled}{2=&cDisabled}/n/n&3Click to Toggle"),
	EDITOR_ITEMSTACK_MENU_VELOCITY_DESCRIPTION ("/n&8» X: &e{1}/n&8» Y: &e{2}/n&8» Z: &e{3}/n/n&3Left Click to Change/n&cShift Right Click to Clear"),
	
	// Velocity menu
	EDITOR_VELOCITY_MENU_TITLE          ("Set Velocity"),
	EDITOR_VELOCITY_MENU_SET_VELOCITY_X ("&bSet X Velocity"),
	EDITOR_VELOCITY_MENU_SET_VELOCITY_Y ("&bSet Y Velocity"),
	EDITOR_VELOCITY_MENU_SET_VELOCITY_Z ("&bSet Z Velocity"),
	
	// Menu Selection
	EDITOR_MENU_SELECTION_TITLE            ("Select a Menu {1}/{2}"),
	EDITOR_MENU_SELECTION_CREATE           ("&bCreate a Menu"),
	EDITOR_MENU_SELECTION_REFRESH          (false, "&bRefresh"),
	EDITOR_MENU_SELECTION_MENU_PREFIX      ("&bMenu: &e"),
	
	EDITOR_MENU_SELECTION_MENU_DESCRIPTION ("&8Title: {1}"),
	
	// Colour Menu
	EDITOR_COLOUR_MENU_TITLE           ("Select or Create a Colour"),
	EDITOR_COLOUR_MENU_SET_WHITE       ("&bWhite"),
	EDITOR_COLOUR_MENU_SET_RED         ("&bRed"),
	EDITOR_COLOUR_MENU_SET_LIME        ("&bLime"),
	EDITOR_COLOUR_MENU_SET_LIGHT_BLUE  ("&bLight Blue"),
	EDITOR_COLOUR_MENU_SET_PINK        ("&bPink"),
	EDITOR_COLOUR_MENU_SET_GRAY        ("&bGrey"),
	EDITOR_COLOUR_MENU_SET_ORANGE      ("&bOrange"),
	EDITOR_COLOUR_MENU_SET_GREEN       ("&bGreen"),
	EDITOR_COLOUR_MENU_SET_BLUE        ("&bBlue"),
	EDITOR_COLOUR_MENU_SET_MAGENTA     ("&bMagenta"),
	EDITOR_COLOUR_MENU_SET_BLACK       ("&bBlack"),
	EDITOR_COLOUR_MENU_SET_YELLOW      ("&bYellow"),
	EDITOR_COLOUR_MENU_SET_BROWN       ("&bBrown"),
	EDITOR_COLOUR_MENU_SET_PURPLE      ("&bPurple"),
	EDITOR_COLOUR_MENU_SET_CYAN        ("&bCyan"),
	EDITOR_COLOUR_MENU_SET_RED_VALUE   ("&bSet Red Value"),
	EDITOR_COLOUR_MENU_SET_GREEN_VALUE ("&bSet Green Value"),
	EDITOR_COLOUR_MENU_SET_BLUE_VALUE  ("&bSet Blue Value"),
	EDITOR_COLOUR_MENU_SET_RANDOM      ("&bRandom Colours"),
	EDITOR_COLOUR_MENU_SET_SIZE        ("&bSet Size"),
	
	EDITOR_COLOUR_MENU_PRESET_DESCRIPTION ("&8Set this particles/n&8colour to {1}/n/n&8» R: &e{2}/n&8» G: &e{3}/n&8» B: &e{4}"),
	EDITOR_COLOUR_MENU_R_DESCRIPTION ("/n&e» R: {1}/n&8• G: &7{2}/n&8• B: &7{3}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1/n&3Shift Click to Adjust by 10"),
	EDITOR_COLOUR_MENU_G_DESCRIPTION ("/n&8• R: &7{1}/n&e» G: {2}/n&8• B: &7{3}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1/n&3Shift Click to Adjust by 10"),
	EDITOR_COLOUR_MENU_B_DESCRIPTION ("/n&8• R: &7{1}/n&8• G: &7{2}/n&e» B: {3}/n/n&3Left Click to Add 1/n&3Right Click to Subtract 1/n&3Shift Click to Adjust by 10"),
	EDITOR_COLOUR_MENU_RANDOM_SUFFIX ("Random"),

	EDITOR_COLOUR_MENU_RANDOM_DESCRIPTION ("&8Give this particle random colours"),

	
	/**
	 * Command Arguments
	 */
	COMMAND_ARGUMENT_NONE (""),
	COMMAND_ARGUMENT_EDIT ("");
	
	private final String defaultValue;
	private final boolean showInFile;
	
	private static Map<String, String> messages;
	
	static 
	{
		messages = new HashMap<String, String>();
		loadMessages();
	}
	
	private Message (String defaultValue)
	{
		this(true, defaultValue);
	}
	
	private Message (boolean showInFile, String defaultValue)
	{
		this.defaultValue = defaultValue;
		this.showInFile = showInFile;
	}
	
	/**
	 * Get this messages colour code translated value<br>
	 * By default each message will have &f applied to the beginning
	 * @return
	 */
	public String getValue () 
	{
		if (messages.containsKey(getKey())) {
			return StringUtil.colorize(messages.get(getKey()));
		}
		return StringUtil.colorize(defaultValue);
	}
	
	/**
	 * Get this messages value without translated colour codes
	 * @return
	 */
	public String getRawValue () 
	{
		if (messages.containsKey(getKey())) {
			return messages.get(getKey());
		}
		return defaultValue;
	}
	
	/**
	 * Get the enum value as a key for the locale configuration file
	 * @return
	 */
	public String getKey () {
		return this.toString().toLowerCase();
	}
	
	/**
	 * Get the message that matches the messageName<br>
	 * Returns UNKNOWN if the messageName has no match
	 * @param messageName
	 * @return
	 */
	public static Message fromString (String messageName)
	{
		try {
			return Message.valueOf(messageName);
		} catch (IllegalArgumentException e) {
			return Message.UNKNOWN;
		}
	}
	
	/**
	 * Reloads all messages found in messages.yml
	 */
	public static void onReload ()
	{
		messages.clear();
		loadMessages();
	}
	
	/**
	 * Loads all messages found in messages.yml
	 */
	private static void loadMessages ()
	{
		CustomConfig locale = Core.instance.getLocaleConfig();
		FileConfiguration config = locale.getConfig();
		
		for (Message message : values())
		{
			String value = config.getString(message.getKey());
			if (value != null) {
				messages.put(message.getKey(), value);
			}
		}
	}
}
