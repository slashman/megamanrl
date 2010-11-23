package crl.data;

import crl.item.*;
public class Items {
		private final static ItemDefinition [] defs = new ItemDefinition[]{
			new ItemDefinition ("PLASMA_BUSTER_C","Cyan Plasma Buster","PLASMA_BUSTER_C",2,"Shoots slow plasma balls",5,0,"",0,1,4,0,10," ","crl.action.PlasmaBuster",false,0,0,0,75,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("LASER_BUSTER_C","Cyan Laser Buster","LASER_BUSTER_C",2,"Shoots a quick laser beam",5,0,"",0,1,2,15,15," ","crl.action.Fire",false,0,0,0,50,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("SLASH_BUSTER_C","Cyan Slash Buster","SLASH_BUSTER_C",2,"Creates a cutting short range beam",5,0,"",0,1,6,2,25," ","crl.action.SlashBuster",false,0,0,0,25,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("PLASMA_BUSTER_M","Magenta Plasma Buster","PLASMA_BUSTER_M",2,"Shoots slow plasma balls",10,0,"",0,1,6,0,10," ","crl.action.PlasmaBuster",false,0,0,0,75,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("LASER_BUSTER_M","Magenta Laser Buster","LASER_BUSTER_M",2,"Shoots a quick laser beam",10,0,"",0,1,4,15,15," ","crl.action.Fire",false,0,0,0,50,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("SLASH_BUSTER_M","Magenta Slash Buster","SLASH_BUSTER_M",2,"Creates a cutting short range beam",10,0,"",0,1,8,2,25," ","crl.action.SlashBuster",false,0,0,0,25,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("PLASMA_BUSTER_Y","Yellow Plasma Buster","PLASMA_BUSTER_Y",2,"Shoots slow plasma balls",15,0,"",0,1,8,0,10," ","crl.action.PlasmaBuster",false,0,0,0,75,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("LASER_BUSTER_Y","Yellow Laser Buster","LASER_BUSTER_Y",2,"Shoots a quick laser beam",15,0,"",0,1,6,15,15," ","crl.action.Fire",false,0,0,0,50,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("SLASH_BUSTER_Y","Yellow Slash Buster","SLASH_BUSTER_Y",2,"Creates a cutting short range beam",15,0,"",0,1,10,2,25," ","crl.action.SlashBuster",false,0,0,0,25,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("PLASMA_BUSTER_K","Alpha Plasma Buster","PLASMA_BUSTER_K",2,"Shoots slow plasma balls",20,0,"",0,1,10,0,10," ","crl.action.PlasmaBuster",false,0,0,0,75,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("LASER_BUSTER_K","Alpha Laser Buster","LASER_BUSTER_K",2,"Shoots a quick laser beam",20,0,"",0,1,8,15,15," ","crl.action.Fire",false,0,0,0,50,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("SLASH_BUSTER_K","Alpha Slash Buster","SLASH_BUSTER_K",2,"Creates a cutting short range beam",20,0,"",0,1,12,2,25," ","crl.action.SlashBuster",false,0,0,0,25,50,false,false,"",2,"",true,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("BUSTER","Buster","BUSTER",2,"Created for Megaman by Dr. Light",1,0,"",0,1,1,15,5," ","crl.action.PlasmaBuster",false,0,0,0,50,50,false,false,"",2,"",false,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("ENERGYTANK","E Tank","ENERGYTANK",0,"Fully Recovers Energy",15,0,"",0,1,0,0,0,"","crl.action.EnergyTank",false,0,0,0,50,50,false,false,"",2,"",true,0,"",false,true,"","",-1,0),
			new ItemDefinition ("ENERGYPELLET","Energy Pellet","ENERGYPELLET",0,"Recover some energy",5,0,"",0,1,0,0,0,"","crl.action.EnergyPellet",false,0,0,0,50,50,false,true,"",2,"",true,0,"",false,true,"","",-1,0),
			new ItemDefinition ("BIGENERGYPELLET","Big Energy Pellet","BIGENERGYPELLET",0,"Recover some energy",10,0,"",0,1,0,0,0,"","crl.action.BigEnergyPellet",false,0,0,0,50,50,false,true,"",2,"",true,0,"",false,true,"","",-1,0),
			new ItemDefinition ("HYPERBOMB","Hyper-Bomb","HYPERBOMB",2,"Shots a powerful timed bomb",99,0,"",0,1,4,4,10," ","crl.action.HyperBomb",false,0,0,0,50,50,false,false,"",2,"",false,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("CUTTER","Rolling Cutter","CUTTER",2,"Shots a sharp cutter",99,0,"",0,1,3,15,10," ","crl.action.PlasmaBuster",false,0,0,0,50,50,false,false,"",2,"",false,0,"",false,true," "," ",-1,0),
			new ItemDefinition ("WEAPONENERGY","Weapon Energy Pellet","WEAPONENERGY",0,"Recover some weapon energy",5,0,"",0,1,0,0,0,"","crl.action.WeaponEnergyPellet",false,0,0,0,50,50,false,true,"",2,"",true,0,"",false,true,"","",-1,0)
	};
	public static ItemDefinition[] getItemDefinitions() {
		return defs;
}
}
