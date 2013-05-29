package clashsoft.mods.morepotions;

import java.util.Random;

import clashsoft.clashsoftapi.CSUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler
{
	
    public void registerRenderInformation()
    {

    }

    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == MorePotionsMod.BrewingStand2_TEID)
        {
        	return new GuiBrewingStand2(player.inventory, (TileEntityBrewingStand2)world.getBlockTileEntity(x, y, z));
        }
        else if (ID == MorePotionsMod.Mixer_TEID)
        {
        	return new GuiMixer(player.inventory, (TileEntityMixer)world.getBlockTileEntity(x, y, z));
        }
        else if (ID == MorePotionsMod.UnbrewingStand_TEID)
        {
        	return new GuiUnbrewingStand(player.inventory, (TileEntityUnbrewingStand)world.getBlockTileEntity(x, y, z));
        }
        return null;
    }

    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int X, int Y, int Z)
    {
        TileEntity te = world.getBlockTileEntity(X, Y, Z);
        if (te != null && te instanceof TileEntityBrewingStand2)
        {
            TileEntityBrewingStand2 bs = (TileEntityBrewingStand2) te;
            return new ContainerBrewingStand2(player.inventory, bs);
        }
        else if (te != null && te instanceof TileEntityMixer)
        {
        	TileEntityMixer m = (TileEntityMixer) te;
            return new ContainerMixer(player.inventory, m);
        }
        else if (te != null && te instanceof TileEntityUnbrewingStand)
        {
        	TileEntityUnbrewingStand ubs = (TileEntityUnbrewingStand) te;
        	return new ContainerUnbrewingStand(player.inventory, ubs);
        }
        else
        {
            return null;
        }
    }

    public World getClientWorld()
    {
        return null;
    }
    
    public void registerRenderers() {}

	public void playSplashEffect(World par0World, int par1, int par2, int par3, ItemStack par4ItemStack)
	{
		Random random = par0World.rand;
    	double d0;
    	double d1;
    	double d2;
    	String s;
    	int j1;
    	int k1;
    	double d3;
    	double d4;
    	double d5;
    	double d6;
    	double d7;

    	d0 = (double)par1;
    	d1 = (double)par2;
    	d2 = (double)par3;
    	s = "iconcrack_" + Item.potion.itemID;

    	for (j1 = 0; j1 < 8; ++j1)
    	{
    		par0World.spawnParticle(s, d0, d1, d2, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
    	}

    	j1 = ItemPotion2.getColorFromItemStack2(par4ItemStack, 0);
    	float f = (float)(j1 >> 16 & 255) / 255.0F;
    	float f1 = (float)(j1 >> 8 & 255) / 255.0F;
    	float f2 = (float)(j1 >> 0 & 255) / 255.0F;
    	String s1 = "spell";

    	if (MorePotionsMod.potion2.isEffectInstant(par4ItemStack))
    	{
    		s1 = "instantSpell";
    	}

    	for (k1 = 0; k1 < 100; ++k1)
    	{
    		d7 = random.nextDouble() * 4.0D;
    		d3 = random.nextDouble() * Math.PI * 2.0D;
    		d4 = Math.cos(d3) * d7;
    		d5 = 0.01D + random.nextDouble() * 0.5D;
    		d6 = Math.sin(d3) * d7;
    		par0World.spawnParticle(s1, d0 + d4 * 0.1D, d1 + 0.3D, d2 + d6 * 0.1D, d4, d5, d6);
    	}
	}

}