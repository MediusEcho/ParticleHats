package com.mediusecho.particlehats.particles;

/**
 * Used to reference hats the player has purchased
 * @author MediusEcho
 *
 */
public class HatReference {

	private final String menuName;
	private final int slot;
	
	public HatReference (final String menuName, final int slot)
	{
		this.menuName = menuName;
		this.slot = slot;
	}
	
	/**
	 * Get the name of the menu this hat is in
	 * @return
	 */
	public String getMenuName () {
		return menuName;
	}
	
	/**
	 * Get the slot this hat is in
	 * @return
	 */
	public int getSlot () {
		return slot;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((menuName == null) ? 0 : menuName.hashCode());
		result = prime * result + slot;
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Hat)) {
			return false;
		}
		
		Hat hat = (Hat)obj;
		
		if (slot != hat.getSlot()) {
			return false;
		}
		
		if (!menuName.equals(hat.getMenu())) {
			return false;
		}
		
		return true;
	}
	
}
