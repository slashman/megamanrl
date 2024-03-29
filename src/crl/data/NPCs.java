package crl.data;

import crl.npc.*;

public class NPCs {
		private final static NPCDefinition [] defs = new NPCDefinition[]{
			new NPCDefinition ("WOMAN0","Woman","WOMAN","VILLAGER", "First thing to do in this town is buy a White Crystal", 1,3,"Argh! You will pay for this!","Somebody, Help me!", false, false),
			new NPCDefinition ("WOMAN1","Woman","WOMAN","VILLAGER", "To restore your life, shout in front of the church", 1,3, "You are evil!", "Don't hurt me, please!", false, false),
			new NPCDefinition ("WOMAN2","Woman","WOMAN","VILLAGER", "Don't look into the Death Star, or you will die.", 1,3, "I will defend myself!", "Argh!!!", false, false),

			new NPCDefinition ("OLDWOMAN0","Old Woman","OLDWOMAN","VILLAGER", "They say a vampire killer is able to use mystic weapons", 1,3, "Don't underestimate me!", "Everybody stay alert!", false, false),
			new NPCDefinition ("OLDWOMAN1","Old Woman","OLDWOMAN","VILLAGER", "I saw the last of the menbeast heading towards the castle", 2,3, "You will pay for this!!!", "Intruder!", false, false),
			new NPCDefinition ("OLDWOMAN2","Old Woman","OLDWOMAN","VILLAGER", "A mysterious girl came by here asking for directions", 1,3, "I won't die alone!", "Help me!! somebody!!", false, false),

			new NPCDefinition ("MAN0","Man","MAN","VILLAGER", "Buy some Garlic. It has special powers", 2,4, "I was far too trusting!", "Hrrmmm", false, false),
			new NPCDefinition ("MAN1","Man","MAN","VILLAGER", "Take my daughter, please!", 2,3, "Defend yourself rogue!","You are stronger than I tought", false, false),
			new NPCDefinition ("MAN2","Man","MAN","VILLAGER", "Dracula Castle is just east of this town", 1,4, "What's wrong with you!", "You sold your powers to chaos!", false, false),

			new NPCDefinition ("OLDMAN0","Old Man","OLDMAN","VILLAGER", "The order of knights sent some men, but they didn't return", 2,3, "To the charge!", "Your soul is dark", false, false),
			new NPCDefinition ("OLDMAN1","Old Man","OLDMAN","VILLAGER", "A brave merchant went to Dracula's castle some months ago", 1,3, "Engarde","End my suffering...", false, false),
			new NPCDefinition ("OLDMAN2","Old Man","OLDMAN","VILLAGER", "They say vampires regain their health drinking blood", 1,4, "To the arms!", "Go ahead and kill me...", false, false),

			new NPCDefinition ("PRIEST","Priest","PRIEST","VILLAGER", "Rest here for a while.", 15,6, "How dare you attack me!", "Your mind will not hold for long", false, true),
			
			new NPCDefinition ("MERCHANT","Merchant","MERCHANT","VILLAGER", "Good day. I don't have anything for you today.", 5,20, "Aha! Yet another one is possessed!", "This is the end... *GASP*", false, false),
			
			new NPCDefinition ("DOG","Dog","DOG","VILLAGER", "ROOF ROOF", 4,4, "GRRRRR!!!", "UUUF... UFF...", false, false),
			
			new NPCDefinition ("VALENTINA","Valentina","HOSTAGE_GIRL","VILLAGER", "Did you come to rescue me? Thanks!", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("MARIE","Marie","HOSTAGE_GIRL","VILLAGER", "It was horrible! I am glad you finally arrived!", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("ANNETE","Annete","HOSTAGE_GIRL","VILLAGER", "I knew you would come! Let's get out of here...", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("LAURA","Laura","HOSTAGE_GIRL","VILLAGER", "Thanks God! I am saved!", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("ROSE","Rose","HOSTAGE_GIRL","VILLAGER", "We are dead... this castle has no way out..", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("BLUE","Blue","HOSTAGE_GIRL","VILLAGER", "You are my last hope...", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("MARCEL","Marsh","HOSTAGE_GIRL","VILLAGER", "Deep inside me, I always knew you would come back", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("PROX","Prox","HOSTAGE_GUY","VILLAGER", "Friend! Lets get the heck out of here!", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("KOREL","Korned","HOSTAGE_GUY","VILLAGER", "Now I have a chance to get out of here!", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("ANUBIS","Anubis","HOSTAGE_GUY","VILLAGER", "This castle is creepy...", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			new NPCDefinition ("ALENOME","Alenome","HOSTAGE_GUY","VILLAGER", "I learned something, there is no light without darkness", 4,10, "Why are you doing this!", "Let my death be swift...", true, false),
			
			new NPCDefinition ("MELDUCK","Melduck","MELDUCK","VILLAGER", "Be on your guard...", 6,25, "What are you doing!", "I shall never have carried you here!", false, false),
			new NPCDefinition ("UNIDED_CLAW","???","CLAW","VILLAGER", "...", 10,40, "Die!", "Argh!", false, false),
			new NPCDefinition ("UNIDED_VINDELITH","???","VINDELITH","VILLAGER", "...", 10,40, "Die!", "Argh!", false, false),
			new NPCDefinition ("CLAW","Claw","CLAW","VILLAGER", "...", 10,40, "Die!", "Argh!", false, false),
			new NPCDefinition ("VINDELITH","Vindelith","VINDELITH","VILLAGER", "...", 10,40, "Die!", "Argh!", false, false),
			new NPCDefinition ("UNIDED_CLARA","???","CLARA","VILLAGER", "...", 10,40, "Die!", "Argh!", false, false),
			
			
			new NPCDefinition ("MAIDEN","???","MAIDEN","VILLAGER", "...", 20,60, "...", "...", false, false),
			new NPCDefinition ("CHRISTRAIN","Christopher","CHRISTOPHER_BELMONT_NPC","VILLAGER", "Go outside for a while my child", 6,25, "Die Monster!", "You dont belong in this world!!", false, false),
			
			new NPCDefinition ("ICEY","Icey","ICEY","VILLAGER", "Name is Icey, Posh Icey. Would you join me for the tea-time? I need my bowler hat first.", 6,25, "For the great Fifur and justice!", "I am terribly posh!", false, false),
			new NPCDefinition ("BARRY","Christopher","BARRETT","VILLAGER", "Call me Barry, I'm this town's artist. Got any good job for me?", 6,25, "Die to my paintbrush!", "In the end, it doesn't even matters..", false, false),
			new NPCDefinition ("LARDA","Larda","LARDA","VILLAGER", "Don't be paranoid... rest here for a night, only 200 in gold? [y/n]", 15,40, "Fall to the power of my Hammer!", "It's a good day to die", false, false),
			new NPCDefinition ("ELI","Eli","WOMAN","VILLAGER", "The mystic key? beats me!", 2,4, "This is completely pointless", "A wasted effort, I will revive!", false, false),
			new NPCDefinition ("MAN3","Man","MAN","VILLAGER", "A crooked trader is offering bum deals in this town.", 2,4, "Somebody help me!", "Just help me!", false, false),
			new NPCDefinition ("MAN4","Man","MAN","VILLAGER", "After Castlevania, I warned you not to return.", 2,4, "Killing me will only reduce your score", "I deserve to die.", false, false),
			new NPCDefinition ("WOMAN3","Woman","WOMAN","VILLAGER", "I want to get to know you better.", 1,3, "I don't want to know anything else!", "Argh!!!", false, false),
			new NPCDefinition ("WOMAN4","Woman","OLDWOMAN","VILLAGER", "Laurels in your soup enhances it's aroma..", 1,3, "Garlic in your soup is awful!", "Argh!!!", false, false),
			new NPCDefinition ("WOMAN5","Woman","WOMAN","VILLAGER", "You look pale my son. You must rest in the church.", 1,3, "You are so mean!", "Argh!!!", false, false),
			
			new NPCDefinition ("CUILOT","Cuilot","MORPHED_LUPINE","VILLAGER", "The time as yet to come", 25,200, "...", "...", false, false),
			new NPCDefinition ("LIONARD","Lionard","MORPHED_WOLF","VILLAGER", "You are not ready yet.", 25,300, "...", "...", false, false),
			
			
			new NPCDefinition ("GRAM","Gram","MAN","VILLAGER", "So, you found me! but there is still not much to see here. Come back later!", 25,150, "Die fool!", "You are good!", false, false),
		};

	public static NPCDefinition[] getNPCDefinitions() {
		return defs;
}
}