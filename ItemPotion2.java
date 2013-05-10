package clashsoft.mods.morepotions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import clashsoft.clashsoftapi.CSUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;

/**
 * @author Clashsoft
 */
public class ItemPotion2 extends Item
{
	private static boolean SHOW_DOUBLE_POTIONS = false;
	private Icon normal;
	public Icon splash;
	private Icon liquid;

	public ItemPotion2(int par1)
	{
		super(par1);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setCreativeTab(CreativeTabs.tabBrewing);
	}

	/**
	 * Returns a list of potion effects for the specified itemstack.
	 */
	public static List<Brewing> getEffects(ItemStack par1ItemStack)
	{
		if (par1ItemStack != null)
		{
			if (par1ItemStack.hasTagCompound() && par1ItemStack.getTagCompound().hasKey("Brewing"))
			{
				List var6 = new ArrayList();
				NBTTagList var3 = par1ItemStack.getTagCompound().getTagList("Brewing");
				boolean var2 = true;
				
				for (int var4 = 0; var4 < var3.tagCount(); ++var4)
				{
					NBTTagCompound var5 = (NBTTagCompound)var3.tagAt(var4);
					Brewing b = Brewing.readFromNBT(var5);
					var6.add(b);
				}
				return var6;
			}
			else
			{
				return brewingTransform(par1ItemStack.getItemDamage(), Item.potion.getEffects(par1ItemStack));
			}
		}
		return new ArrayList();
	}

	private static List brewingTransform(int par1, List<PotionEffect> par2List)
	{
		if (par2List != null && par2List.size() > 0)
		{
			List<Brewing> ret = new ArrayList<Brewing>();
			for (PotionEffect effect : par2List)
			{
				ret.add(new Brewing(effect, 0, effect.getDuration(), Brewing.awkward));
			}
			return ret;
		}
		return new ArrayList();
	}

	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if (!par3EntityPlayer.capabilities.isCreativeMode)
		{
			--par1ItemStack.stackSize;
		}

		if (!par2World.isRemote)
		{
			List var4 = this.getEffects(par1ItemStack);

			if (var4 != null)
			{
				Iterator var5 = var4.iterator();

				while (var5.hasNext())
				{
					Brewing var6 = (Brewing)var5.next();
					if (var6.getEffect() != null)
					{
						par3EntityPlayer.addPotionEffect(var6.getEffect());
					}
				}
			}
		}

		if (!par3EntityPlayer.capabilities.isCreativeMode)
		{
			if (par1ItemStack.stackSize <= 0)
			{
				return new ItemStack(Item.glassBottle);
			}

			par3EntityPlayer.inventory.addItemStackToInventory(new ItemStack(Item.glassBottle));
		}

		return par1ItemStack;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack)
	{
		return 32;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack)
	{
		return EnumAction.drink;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if (isSplash(par1ItemStack.getItemDamage()))
		{
			if (!par3EntityPlayer.capabilities.isCreativeMode)
			{
				--par1ItemStack.stackSize;
			}

			par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if (!par2World.isRemote)
			{
				par2World.spawnEntityInWorld(new EntityPotion2(par2World, par3EntityPlayer, par1ItemStack));
			}

			return par1ItemStack;
		}
		else
		{
			par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
			return par1ItemStack;
		}
	}

	/**
	 * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
	 * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
	 */
	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Gets an icon index based on an item's damage value
	 */
	public Icon getIconFromDamage(int par1)
	{
		return isSplash(par1) ? splash : normal;
	}


	@SideOnly(Side.CLIENT)
	/**
	 * Gets an icon index based on an item's damage value and the given render pass
	 */
	public Icon getIconFromDamageForRenderPass(int par1, int par2)
	{
		return par2 == 0 ? this.liquid : super.getIconFromDamageForRenderPass(par1, par2);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.normal = par1IconRegister.registerIcon("potion");
		this.splash = par1IconRegister.registerIcon("potion_splash");
		this.liquid = par1IconRegister.registerIcon("potion_contents" + (MorePotionsMod.animatedPotionLiquid ? "_anim" : ""));
	}

	/**
	 * returns wether or not a potion is a throwable splash potion based on damage value
	 */
	public static boolean isSplash(int par1)
	{
		return par1 == 2 ? true : par1 == 1 ? false : ItemPotion.isSplash(par1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
	{
		return getColorFromItemStack2(par1ItemStack, par2);
	}

	public static int getColorFromItemStack2(ItemStack par1ItemStack, int par2)
	{
		if (par2 == 0 && par1ItemStack != null)
		{
			if (par1ItemStack.getItemDamage() == 0)
			{
				return 0x0C0CFF;
			}
			List effects = getEffects(par1ItemStack);
			if (effects != null && effects.size() > 0)
			{
				int[] i1 = new int[effects.size()];

				int i = 0;
				for (int j = 0; j < effects.size(); j++)
				{
					Brewing b = (Brewing)effects.get(j);
					i += b.getLiquidColor();
					i1[j] = ((Brewing)effects.get(j)).getLiquidColor();
				}
				i /= effects.size() != 0 ? effects.size() : 1;
				return CSUtil.combineColors(i1);
			}
			else
			{
				return (0x0C0CFF);
			}
		}
		else
		{
			return 16777215;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	@Override
	public String getItemDisplayName(ItemStack par1ItemStack)
	{
		List effects = getEffects(par1ItemStack);
		if (par1ItemStack.getItemDamage() == 0)
		{
			return StatCollector.translateToLocal("item.emptyPotion.name").trim();
		}
		else
		{
			String var2 = "";

			if (isSplash(par1ItemStack.getItemDamage()))
			{
				var2 = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
			}

			List<Brewing> var3 = this.getEffects(par1ItemStack);
			String var4 = "";

			if (var3 != null && !var3.isEmpty())
			{
				if (var3.size() == Brewing.combinableEffects.size())
				{
					return "\u00a7b" + var2 + StatCollector.translateToLocal("potion.alleffects.postfix");
				}
				else if (var3.size() > 3)
				{
					return var2 + StatCollector.translateToLocal("potion.potionof") + " " + var3.size() + " " + StatCollector.translateToLocal("potion.effects");
				}
				else if (var3.get(0).isBase())
				{
					return StatCollector.translateToLocal("potion.prefix." + ((BrewingBase)var3.get(0)).basename).trim() + " " + var2 + super.getItemDisplayName(par1ItemStack);
				}
				for(int i = 0; i < var3.size(); i++)
				{
					if (i == 0)
					{
						var4 = StatCollector.translateToLocal(((Brewing)var3.get(i)).getEffect() != null && ((Brewing)var3.get(i)).getEffect().getPotionID() > 0 ? (((Brewing)var3.get(i)).getEffect().getEffectName() + ".postfix") : "");
						var2 += StatCollector.translateToLocal(var4).trim();
					}
					else if (i + 1 == var3.size())
					{
						var4 = ((Brewing)var3.get(i)).getEffect().getEffectName();
						var2 += " " + StatCollector.translateToLocal("potion.and") + " " + StatCollector.translateToLocal(var4).trim();
					}
					else
					{
						var4 = ((Brewing)var3.get(i)).getEffect().getEffectName();
						var2 += ", " + StatCollector.translateToLocal(var4).trim();
					}
				}
				return var2;
			}
			else
			{
				return "Unknown";
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
		if (par1ItemStack.getItemDamage() != 0)
		{
			List<Brewing> var5 = this.getEffects(par1ItemStack);

			if (var5 != null && !var5.isEmpty())
			{
				Iterator var9 = var5.iterator();
				int badEffects = 0;
				while (var9.hasNext())
				{
					Brewing var7 = (Brewing)var9.next();
					String var8 = (var7.getEffect() != null && var7.getEffect().getPotionID() > 0 ? StatCollector.translateToLocal(var7.getEffect().getEffectName()) : "\u00a77" + StatCollector.translateToLocal("potion.empty")).trim();
					
					if (var7.getEffect() != null && var7.getEffect().getAmplifier() > 0)
					{
						var8 = var8 + " " + StatCollector.translateToLocal("potion.potency." + var7.getEffect().getAmplifier()).trim();
					}

					if (var7.getEffect() != null && var7.getEffect().getDuration() > 20)
					{
						var8 = var8 + " (" + Potion.getDurationString(var7.getEffect()) + ")";
					}
					
					if (var7.isBadEffect())
					{
						badEffects++;
						par3List.add("\u00a7c" + var8);
					}
					else
					{
						par3List.add("\u00a7a" + var8);
					}
				}
				if (MorePotionsMod.advancedPotionInfo)
				{
					if (var5.size() > 1)
					{
						if (var5.size() - badEffects > 1)
						{
							int goodEffects = var5.size() - badEffects;
							float goodEffectsPercentage = (float)goodEffects / (float)var5.size() * 100;
							String color = CSUtil.fontColor("green") + "\u00a7o";
							par3List.add(CSUtil.fontColor("lightgray") + "\u00a7o" + StatCollector.translateToLocal("potion.goodeffects") + ": " + color + goodEffects + " (" + String.format("%.1f", goodEffectsPercentage) + "%)");
						}
						if (badEffects > 1)
						{
							float badEffectsPercentage = (float)badEffects / (float)var5.size() * 100;
							String color = CSUtil.fontColor("red") + "\u00a7o";
							par3List.add(CSUtil.fontColor("lightgray") + "\u00a7o" + StatCollector.translateToLocal("potion.negativeEffects") + ": " + color + badEffects + " (" + String.format("%.1f", badEffectsPercentage) + "%)");
						}
						
						int maxDuration = 0;
						for (int i = 0; i < var5.size(); i++)
						{
							if (var5.get(i).getEffect() != null && var5.get(i).getEffect().getDuration() > maxDuration)
							{
								maxDuration = var5.get(i).getEffect().getDuration();
							}
						}
						par3List.add(CSUtil.fontColor("lightgray") + "\u00a7o" + StatCollector.translateToLocal("potion.highestduration") + ": " + CSUtil.fontColor("darkgray") + "\u00a7o" + Potion.getDurationString(new PotionEffect(0, maxDuration, 0)));
						
						int averageDuration = 0;
						for (int i = 0; i < var5.size(); i++)
						{
							if (var5.get(i).getEffect() != null)
							{
								averageDuration += var5.get(i).getEffect().getDuration();
							}
						}
						averageDuration /= var5.size();
						par3List.add(CSUtil.fontColor("lightgray") + "\u00a7o" + StatCollector.translateToLocal("potion.averageduration") + ": " + CSUtil.fontColor("darkgray") + "\u00a7o" + Potion.getDurationString(new PotionEffect(0, averageDuration, 0)));
						
						int maxAmplifier = 0;
						for (int i = 0; i < var5.size(); i++)
						{
							if (var5.get(i).getEffect() != null && var5.get(i).getEffect().getAmplifier() > maxAmplifier)
							{
								maxAmplifier = var5.get(i).getEffect().getAmplifier();
							}
						}
						if (maxAmplifier > 0)
						{
							par3List.add(CSUtil.fontColor("lightgray") + "\u00a7o" + StatCollector.translateToLocal("potion.highestamplifier") + ": " + CSUtil.fontColor("darkgray") + "\u00a7o" + StatCollector.translateToLocal("potion.potency." + maxAmplifier));
						}
						
						int averageAmplifier = 0;
						for (int i = 0; i < var5.size(); i++)
						{
							if (var5.get(i).getEffect() != null)
							{
								averageAmplifier += var5.get(i).getEffect().getAmplifier() + 1;
							}
						}
						averageAmplifier /= var5.size();
						averageAmplifier -= 1;
						if (averageAmplifier > 0)
						{
							par3List.add(CSUtil.fontColor("lightgray") + "\u00a7o" + StatCollector.translateToLocal("potion.averageamplifier") + ": " + CSUtil.fontColor("darkgray") + "\u00a7o" + StatCollector.translateToLocal("potion.potency." + averageAmplifier));
						}
					}
					if (Brewing.getExperience(par1ItemStack) > 0.3F)
					{
						par3List.add(CSUtil.fontColor("lightgray") + "\u00a7o" + StatCollector.translateToLocal("potion.value") + ": " + CSUtil.fontColor("yellow") + "\u00a7o" + String.format("%.2f", Brewing.getExperience(par1ItemStack) * 100 / 223.9F));
					}
				}
			}
			else
			{
				String var6 = StatCollector.translateToLocal("potion.empty").trim();
				par3List.add("\u00a77" + var6);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack par1ItemStack)
	{
		List var2 = this.getEffects(par1ItemStack);
		return var2 != null && !var2.isEmpty() && ((Brewing)var2.get(0)).getEffect() != null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(this, 1, 0));
		ItemStack allEffects1 = new ItemStack(this, 1, 1);
		ItemStack allEffects2 = new ItemStack(this, 1, 2);
		ItemStack good1 = new ItemStack(this, 1, 1);
		ItemStack good2 = new ItemStack(this, 1, 2);
		ItemStack bad1 = new ItemStack(this, 1, 1);
		ItemStack bad2 = new ItemStack(this, 1, 2);
		
		for (BrewingBase brewing : Brewing.baseBrewings)
		{
			for (int i = 1; i <= 2; i++)
			{
				par3List.add(brewing.addBrewingToItemStack(new ItemStack(this, 1, i)));
			}
		}
		for (Brewing brewing : Brewing.effectBrewings)
		{
			for (int i = 1; i <= 2; i++)
			{
				for (Brewing brewing2 : brewing.getSubTypes())
				{
					Brewing var1 = new Brewing(brewing2.getEffect(), brewing2.getMaxAmplifier(), brewing2.getMaxDuration(), brewing2.getOpposite(), brewing2.getIngredient(), brewing2.getBase());
					if (i == 2 && var1 != null && var1.getEffect() != null && var1.getEffect().getPotionID() > 0)
					{
						var1.setEffect(new PotionEffect(var1.getEffect().getPotionID(), MathHelper.ceiling_double_int(var1.getEffect().getDuration() * 0.75D), var1.getEffect().getAmplifier()));
					}
					par3List.add(var1.addBrewingToItemStack(new ItemStack(this, 1, i)));
				}
			}
		}
		if (MorePotionsMod.multiPotions)
		{
			for (int i = 1; i <= 2; i++)
			{
				for (Brewing brewing1 : Brewing.combinableEffects)
				{
					for (Brewing brewing2 : Brewing.combinableEffects)
					{
						if (brewing1 != brewing2)
						{
							Brewing var1 = new Brewing(brewing1.getEffect(), brewing1.getMaxAmplifier(), brewing1.getMaxDuration(), brewing1.getOpposite(), brewing1.getIngredient(), brewing1.getBase());
							Brewing var2 = new Brewing(brewing2.getEffect(), brewing2.getMaxAmplifier(), brewing2.getMaxDuration(), brewing2.getOpposite(), brewing2.getIngredient(), brewing2.getBase());
							if (i == 2 && var1 != null && var1.getEffect() != null && var1.getEffect().getPotionID() > 0)
							{
								var1.setEffect(new PotionEffect(var1.getEffect().getPotionID(), MathHelper.ceiling_double_int(var1.getEffect().getDuration() * 0.75D), var1.getEffect().getAmplifier()));
							}
							if (i == 2 && var2 != null && var2.getEffect() != null && var2.getEffect().getPotionID() > 0)
							{
								var2.setEffect(new PotionEffect(var2.getEffect().getPotionID(), MathHelper.ceiling_double_int(var2.getEffect().getDuration() * 0.75D), var2.getEffect().getAmplifier()));
							}
							par3List.add(var2.addBrewingToItemStack(var1.addBrewingToItemStack(new ItemStack(this, 1, i))));
						}
					}
				}
			}
		}
		for (Brewing brewing : Brewing.goodEffects)
		{
			if (brewing != brewing.effectRemove)
			{
				good1 = brewing.addBrewingToItemStack(good1);
				good2 = brewing.addBrewingToItemStack(good2);
			}
		}
		for (Brewing brewing : Brewing.badEffects)
		{
			if (brewing != brewing.effectRemove)
			{
				bad1 = brewing.addBrewingToItemStack(bad1);
				bad2 = brewing.addBrewingToItemStack(bad2);
			}
		}
		for (Brewing brewing : Brewing.combinableEffects)
		{
			allEffects1 = brewing.addBrewingToItemStack(allEffects1);
			allEffects2 = brewing.addBrewingToItemStack(allEffects2);
		}
		
		par3List.add(allEffects1);
		par3List.add(allEffects2);
		par3List.add(good1);
		par3List.add(good2);
		par3List.add(bad1);
		par3List.add(bad2);
		/*ItemStack skyPotion = Brewing.digSpeed.addBrewingToItemStack(Brewing.heal.addBrewingToItemStack(new ItemStack(this, 1, i)));
    		skyPotion.setItemName("\u00a7eSky's Butter Potion"); //6, e
    		par3List.add(skyPotion);*/
	}
	
	@SideOnly(Side.CLIENT)
    public static Icon func_94589_d(String par0Str)
    {
        return par0Str == "potion" ? MorePotionsMod.potion2.normal : (par0Str == "potion_splash" ? MorePotionsMod.potion2.splash : (par0Str == "potion_contents" ? MorePotionsMod.potion2.liquid : null));
    }

	public boolean isEffectInstant(ItemStack par1ItemStack)
	{
		Brewing b = Brewing.getBrewingFromItemStack(par1ItemStack);
		return b != null ? (b.getEffect() != null && b.getEffect().getPotionID() > 0 ? Potion.potionTypes[b.getEffect().getPotionID()].isInstant() : false) : false;
	}
	
	@Override
	public Entity createEntity(World world, Entity entity, ItemStack itemstack)
	{
		if (entity instanceof EntityPlayer && isSplash(itemstack.getItemDamage()))
		{
			if (!((EntityPlayer)entity).capabilities.isCreativeMode)
			{
				--itemstack.stackSize;
			}

			world.playSoundAtEntity(((EntityPlayer)entity), "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
			Entity e = new EntityPotion2(world, ((EntityPlayer)entity), itemstack);

			if (!world.isRemote)
			{
				world.spawnEntityInWorld(e);
			}

			return e;
		}
		return null;
	}
}
