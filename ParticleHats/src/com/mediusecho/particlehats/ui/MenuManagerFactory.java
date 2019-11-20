package com.mediusecho.particlehats.ui;

import com.mediusecho.particlehats.ParticleHats;
import com.mediusecho.particlehats.editor.EditorMenuManager;
import com.mediusecho.particlehats.player.PlayerState;

public class MenuManagerFactory {

	private final ParticleHats core;
	
	public MenuManagerFactory (final ParticleHats core)
	{
		this.core = core;
	}
	
	/**
	 * Returns a new StaticMenuManager class, unregisters any existing menu manager classes
	 * @param playerState
	 * @return
	 */
	public StaticMenuManager getStaticMenuManager (PlayerState playerState)
	{
		MenuManager menuManager = playerState.getMenuManager();
		if (menuManager == null) 
		{
			StaticMenuManager staticMenuManager = new StaticMenuManager(core, playerState.getOwner());

			playerState.setMenuManager(staticMenuManager);
			return staticMenuManager;
		}
		
		else if (!(menuManager instanceof StaticMenuManager)) 
		{
			menuManager.willUnregister();
			
			StaticMenuManager staticMenuManager = new StaticMenuManager(core, playerState.getOwner());

			playerState.setMenuManager(staticMenuManager);
			return staticMenuManager;
		}
		
		return (StaticMenuManager)menuManager;
	}
	
	/**
	 * Returns a new EditorMenuManager class, unregisters any existing menu manager classes
	 * @param playerState
	 * @return
	 */
	public EditorMenuManager getEditorMenuManager (PlayerState playerState)
	{
		MenuManager menuManager = playerState.getMenuManager();
		if (menuManager == null)
		{
			EditorMenuManager editorManager = new EditorMenuManager(core, playerState.getOwner());
			
			playerState.setMenuManager(editorManager);
			return editorManager;
		}
		
		else if (!(menuManager instanceof EditorMenuManager))
		{
			menuManager.willUnregister();
			
			EditorMenuManager editorManager = new EditorMenuManager(core, playerState.getOwner());
			
			playerState.setMenuManager(editorManager);
			return editorManager;
		}
		
		return (EditorMenuManager)menuManager;
	}
}
