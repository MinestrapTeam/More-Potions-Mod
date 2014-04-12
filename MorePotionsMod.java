package clashsoft.mods.morepotions;

import clashsoft.brewingapi.BrewingAPI;
import clashsoft.cslib.minecraft.CSLib;
import clashsoft.cslib.minecraft.ClashsoftMod;
import clashsoft.cslib.minecraft.block.CSBlocks;
import clashsoft.cslib.minecraft.crafting.CSCrafting;
import clashsoft.cslib.minecraft.item.CSItems;
import clashsoft.cslib.minecraft.item.CSStacks;
import clashsoft.cslib.minecraft.item.CustomItem;
import clashsoft.cslib.minecraft.potion.CustomPotion;
import clashsoft.cslib.minecraft.update.CSUpdate;
import clashsoft.cslib.minecraft.util.CSConfig;
import clashsoft.cslib.util.CSString;
import clashsoft.mods.morepotions.block.BlockCauldron2;
import clashsoft.mods.morepotions.block.BlockMixer;
import clashsoft.mods.morepotions.block.BlockUnbrewingStand;
import clashsoft.mods.morepotions.common.MPMEventHandler;
import clashsoft.mods.morepotions.common.MPMProxy;
import clashsoft.mods.morepotions.item.ItemMortar;
import clashsoft.mods.morepotions.network.MPMNetHandler;
import clashsoft.mods.morepotions.potion.MPMEffectHandler;
import clashsoft.mods.morepotions.potion.MPMIngredientHandler;
import clashsoft.mods.morepotions.potion.MPMPotionList;
import clashsoft.mods.morepotions.tileentity.TileEntityCauldron;
import clashsoft.mods.morepotions.tileentity.TileEntityMixer;
import clashsoft.mods.morepotions.tileentity.TileEntityUnbrewingStand;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = MorePotionsMod.MODID, name = MorePotionsMod.NAME, version = MorePotionsMod.VERSION, dependencies = MorePotionsMod.DEPENDENCIES)
public class MorePotionsMod extends ClashsoftMod<MPMNetHandler>
{
	public static final String				MODID						= "morepotions";
	public static final String				NAME						= "More Potions Mod";
	public static final String				ACRONYM						= "mpm";
	public static final String				DEPENDENCIES				= CSLib.DEPENDENCY + ";required-after:" + BrewingAPI.MODID;
	public static final String				VERSION						= CSUpdate.CURRENT_VERSION + "-1.0.0-pre";
	
	@Instance(MODID)
	public static MorePotionsMod			instance;
	
	@SidedProxy(clientSide = "clashsoft.mods.morepotions.client.MPMClientProxy", serverSide = "clashsoft.mods.morepotions.common.MPMProxy")
	public static MPMProxy					proxy;
	
	public static final ResourceLocation	customEffects				= new ResourceLocation("morepotions", "textures/gui/potions.png");
	
	public static int						randomMode					= 0;
	
	public static int						mixerTileEntityID			= 12;
	public static int						cauldronTileEntityID		= 13;
	public static int						unbrewingStandTileEntityID	= 14;
	
	public static boolean					cauldronInfo				= false;
	
	public static CustomPotion				effectRemove				= new CustomPotion("potion.effect_removing", 0xFFFFFF, false).setIcon(customEffects, 1, 0);
	public static CustomPotion				waterWalking				= new CustomPotion("potion.water_walking", 0x124EFE, false).setIcon(customEffects, 2, 0);
	public static CustomPotion				coldness					= new CustomPotion("potion.coldness", 0x00DDFF, false).setIcon(customEffects, 3, 0);
	public static CustomPotion				ironSkin					= new CustomPotion("potion.iron_skin", 0xD8D8D8, false).setIcon(customEffects, 4, 0);
	public static CustomPotion				obsidianSkin				= new CustomPotion("potion.obsidian_skin", 0x101023, false).setIcon(customEffects, 5, 0);
	public static CustomPotion				doubleJump					= new CustomPotion("potion.double_jump", 0x157490, false).setIcon(customEffects, 6, 0);
	public static CustomPotion				doubleLife					= new CustomPotion("potion.double_life", 0xFF2222, false).setIcon(customEffects, 7, 0);
	public static CustomPotion				explosiveness				= new CustomPotion("potion.explosiveness", 0xCC0000, true).setIcon(customEffects, 1, 1);
	public static CustomPotion				random						= new CustomPotion("potion.random", 0x000000, false).setIcon(customEffects, 2, 1);
	public static CustomPotion				thorns						= new CustomPotion("potion.thorns", 0x810081, false).setIcon(customEffects, 3, 1);
	public static CustomPotion				greenThumb					= new CustomPotion("potion.green_thumb", 0x008100, false).setIcon(customEffects, 4, 1);
	public static CustomPotion				projectile					= new CustomPotion("potion.projectile", 0x101010, false).setIcon(customEffects, 5, 1);
	
	public static BlockMixer				mixer;
	public static BlockCauldron2			cauldron2;
	public static BlockUnbrewingStand		unbrewingStand;
	
	public static ItemReed					cauldronItem2;
	public static Item						dust;
	public static Item						mortar;
	
	public static ItemStack					dustCoal;
	public static ItemStack					dustIron;
	public static ItemStack					dustGold;
	public static ItemStack					dustObsidian;
	public static ItemStack					dustDiamond;
	public static ItemStack					dustEmerald;
	public static ItemStack					dustQuartz;
	public static ItemStack					dustWither;
	public static ItemStack					dustEnderpearl;
	public static ItemStack					dustClay;
	public static ItemStack					dustBrick;
	public static ItemStack					dustFlint;
	public static ItemStack					dustGlass;
	public static ItemStack					dustCharcoal;
	public static ItemStack					dustWoodOak;
	public static ItemStack					dustWoodBirch;
	public static ItemStack					dustWoodSpruce;
	public static ItemStack					dustWoodJungle;
	public static ItemStack					dustNetherstar;
	public static ItemStack					dustNetherbrick;
	
	public MorePotionsMod()
	{
		super(proxy, MODID, NAME, ACRONYM, VERSION);
		this.hasConfig = true;
		this.netHandlerClass = MPMNetHandler.class;
		this.eventHandler = new MPMEventHandler();
		this.url = "https://github.com/Clashsoft/More-Potions-Mod/wiki";
	}
	
	@Override
	public void readConfig()
	{
		randomMode = CSConfig.getInt("potions", "RandomPotionMode", "Determines how the random potion works, if this is 0 the effect is instant and you get a random potion effect when you drink the potion, 1 will give you a new effect every 2 seconds.", 0);
		cauldronInfo = CSConfig.getBool("cauldrons", "CauldronInfo", true);
		random.setIsInstant(randomMode == 0);
	}
	
	@Override
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		
		cauldron2 = (BlockCauldron2) new BlockCauldron2().setBlockName("cauldron").setBlockTextureName("cauldron");
		mixer = (BlockMixer) new BlockMixer().setBlockName("mixer");
		unbrewingStand = (BlockUnbrewingStand) new BlockUnbrewingStand().setBlockName("unbrewing_stand");
		
		cauldronItem2 = (ItemReed) new ItemReed(cauldron2).setUnlocalizedName("cauldron").setTextureName("cauldron").setCreativeTab(CreativeTabs.tabBrewing);
		mortar = new ItemMortar().setUnlocalizedName("mortar").setTextureName("morepotions:mortar");
		
		String[] dusts = new String[] { "coal", "iron", "gold", "diamond", "emerald", "obsidian", "quartz", "wither", "ender_pearl", "clay", "brick", "flint", "glass", "charcoal", "oak_wood", "birch_wood", "spruce_wood", "jungle_wood", "nether_star", "nether_brick" };
		dust = new CustomItem(dusts, CSString.concatAll(dusts, "morepotions:", "_dust"), new CreativeTabs[] { CreativeTabs.tabMaterials });
		
		CSBlocks.replaceBlock(Blocks.cauldron, cauldron2);
		CSBlocks.addBlock(mixer, ItemBlock.class, "mixer");
		CSBlocks.addBlock(unbrewingStand, ItemBlock.class, "unbrewing_stand");
		
		CSItems.replaceItem(Items.cauldron, cauldronItem2);
		CSItems.addItem(mortar, "mortar");
		CSItems.addItem(dust, "dust");
		
		BrewingAPI.setPotionList(MPMPotionList.instance);
	}
	
	@Override
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		
		BrewingAPI.registerEffectHandler(new MPMEffectHandler());
		BrewingAPI.registerIngredientHandler(new MPMIngredientHandler());
		
		GameRegistry.registerTileEntityWithAlternatives(TileEntityMixer.class, "Mixer", "Mixxer");
		GameRegistry.registerTileEntity(TileEntityCauldron.class, "Cauldron2");
		GameRegistry.registerTileEntity(TileEntityUnbrewingStand.class, "UnbrewingStand");
		
		this.addRecipes();
	}
	
	@Override
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
	}
	
	private void addRecipes()
	{
		CSCrafting.addRecipe(new ItemStack(mortar), new Object[] { "SfS", " S ", 'S', Blocks.stone, 'f', Items.flint });
		CSCrafting.addRecipe(new ItemStack(mixer), new Object[] { "gSg", "g g", "SiS", 'g', Blocks.glass_pane, 'S', Blocks.stone, 'i', Items.iron_ingot });
		CSCrafting.addRecipe(new ItemStack(unbrewingStand), new Object[] { "bib", "bpb", "bbb", 'b', Items.brick, 'i', Items.iron_ingot, 'p', Items.glass_bottle });
		
		ItemStack mortarStack = new ItemStack(mortar, 1, OreDictionary.WILDCARD_VALUE);
		
		dustCoal = CSCrafting.registerOre("dustCoal", new ItemStack(dust, 1, 0));
		dustIron = CSCrafting.registerOre("dustIron", new ItemStack(dust, 1, 1));
		dustGold = CSCrafting.registerOre("dustGold", new ItemStack(dust, 1, 2));
		dustDiamond = CSCrafting.registerOre("dustDiamond", new ItemStack(dust, 1, 3));
		dustEmerald = CSCrafting.registerOre("dustEmerald", new ItemStack(dust, 1, 4));
		dustObsidian = CSCrafting.registerOre("dustObsidian", new ItemStack(dust, 1, 5));
		dustQuartz = CSCrafting.registerOre("dustQuartz", new ItemStack(dust, 1, 6));
		dustWither = CSCrafting.registerOre("dustWither", new ItemStack(dust, 1, 7));
		dustEnderpearl = CSCrafting.registerOre("dustEnderpearl", new ItemStack(dust, 1, 8));
		dustClay = CSCrafting.registerOre("dustClay", new ItemStack(dust, 1, 9));
		dustBrick = CSCrafting.registerOre("dustBrick", new ItemStack(dust, 1, 10));
		dustFlint = CSCrafting.registerOre("dustFlint", new ItemStack(dust, 1, 11));
		dustGlass = CSCrafting.registerOre("dustGlass", new ItemStack(dust, 1, 12));
		dustCharcoal = CSCrafting.registerOre("dustCharcoal", new ItemStack(dust, 1, 13));
		dustWoodOak = CSCrafting.registerOre("dustWoodOak", CSCrafting.registerOre("dustWood", new ItemStack(dust, 1, 14)));
		dustWoodBirch = CSCrafting.registerOre("dustWoodBirch", CSCrafting.registerOre("dustWood", new ItemStack(dust, 1, 15)));
		dustWoodSpruce = CSCrafting.registerOre("dustWoodSpruce", CSCrafting.registerOre("dustWood", new ItemStack(dust, 1, 16)));
		dustWoodJungle = CSCrafting.registerOre("dustWoodJungle", CSCrafting.registerOre("dustWood", new ItemStack(dust, 1, 17)));
		dustNetherstar = CSCrafting.registerOre("dustNetherstar", new ItemStack(dust, 1, 18));
		dustNetherbrick = CSCrafting.registerOre("dustNetherbrick", new ItemStack(dust, 1, 19));
		
		CSCrafting.addShapelessRecipe(dustCoal, CSStacks.coal, mortarStack);
		CSCrafting.addShapelessRecipe(dustIron, CSStacks.iron_ingot, mortarStack);
		CSCrafting.addShapelessRecipe(dustGold, CSStacks.gold_ingot, mortarStack);
		CSCrafting.addShapelessRecipe(dustDiamond, CSStacks.diamond, mortarStack);
		CSCrafting.addShapelessRecipe(dustEmerald, CSStacks.emerald, mortarStack);
		CSCrafting.addShapelessRecipe(dustObsidian, CSStacks.obsidian, mortarStack);
		CSCrafting.addShapelessRecipe(dustQuartz, CSStacks.quartz, mortarStack);
		CSCrafting.addShapelessRecipe(dustWither, CSStacks.wither_skull, dustCoal, dustQuartz);
		CSCrafting.addShapelessRecipe(dustEnderpearl, CSStacks.ender_pearl, mortarStack);
		CSCrafting.addShapelessRecipe(dustClay, CSStacks.clay, mortarStack);
		CSCrafting.addShapelessRecipe(dustBrick, CSStacks.brick, mortarStack);
		CSCrafting.addShapelessRecipe(dustFlint, CSStacks.flint, mortarStack);
		CSCrafting.addShapelessRecipe(dustGlass, CSStacks.glass_block, mortarStack);
		CSCrafting.addShapelessRecipe(dustCharcoal, CSStacks.char_coal, mortarStack);
		CSCrafting.addShapelessRecipe(dustWoodOak, CSStacks.oak_planks, mortarStack);
		CSCrafting.addShapelessRecipe(dustWoodBirch, CSStacks.birch_planks, mortarStack);
		CSCrafting.addShapelessRecipe(dustWoodSpruce, CSStacks.spruce_planks, mortarStack);
		CSCrafting.addShapelessRecipe(dustWoodJungle, CSStacks.jungle_planks, mortarStack);
		CSCrafting.addShapelessRecipe(dustNetherstar, CSStacks.nether_star, mortarStack);
		CSCrafting.addShapelessRecipe(dustNetherbrick, CSStacks.nether_brick, mortarStack);
		
		CSCrafting.addFurnaceRecipe(dustIron, CSStacks.iron_ingot, 0F);
		CSCrafting.addFurnaceRecipe(dustGold, CSStacks.gold_ingot, 0F);
		CSCrafting.addFurnaceRecipe(dustQuartz, CSStacks.quartz, 0F);
		CSCrafting.addFurnaceRecipe(dustClay, CSStacks.brick, 0.1F);
		CSCrafting.addFurnaceRecipe(dustBrick, CSStacks.brick, 0F);
		CSCrafting.addFurnaceRecipe(dustGlass, CSStacks.glass_block, 0F);
	}
}