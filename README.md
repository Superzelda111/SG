# SG
Dedicated survivalgames plugin made for anyone out there who wants a special touch on the exciting gamemode!

### What makes this plugin special?
It is firstly made to run only one match per server, and is therefore supposed to be used with BungeeCord. There is a setting for this in the config however.
- Drops at random locations, but with announcements before
- All the items have custom lores and enchantments to give the user a better experience
- Sponsorship: Uses players points to buy items for a user ingame from a menu
- Easy setup
- Open source!

### Setup
Setting up this plugin is quite simple. All you need is the plugin itself, some arenas, and a server that restarts itself or multicraft host.  
1. Copy all arenas into your server root folder, and make sure all the names are short and have no strings in it. (sg1,sg2,sge) 1
2. Go ingame and type /addmap <filename> <name>. For filename, put the name you made in step 1, and name, still in only one string(todo) a name for the arena. 2
3. Type /editarena <filename>. For filename put the arena filename, the name of the folder containing the map. 3
4. Go to every pod/spawn and make sure the player can run straight ahead, without jumping. 4
5. Type /addspawn <filename> <index>. For index put 0-23. Repeat step for every spawn. I recommend starting with 0 and working your way to 23. 5
6. Do any other changes to the map if wanted, and finally type /savearena <filename>. 6
7. Teleport to the location of your lobby by writing /tploc <world> <x> <y> <z> 7
8. Set lobby with /setlobby. 8
