package LuckDraw.utils;


import LuckDraw.Luck;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class LuckFile {

    private static LuckFile file;
    private String name;
    private LuckFile(String name){
        this.name = name;
        file = this;
    }

    public static LuckFile getFile(String name){
        new LuckFile(name);
        return file;
    }

    private void createConfig(LinkedHashMap<String,Object> config){
        Config config1 = this.getConfigs();
        config1.setAll(config);
        config1.save();
    }

    private Config getConfigs(){
        return new Config(getLuckFile(name),Config.YAML);
    }

    private static Config getLuckConfig(String name){
        return isNotNull(name)?new Config(getLuckFile(name),Config.YAML):null;
    }

    private static boolean isNotNull(String name){
        return getLuckFile(name).exists();
    }

    private static File getLuckFile(String name){
        return new File(Luck.getLuck().getDataFolder()+"/LuckDraw/"+name+".yml");
    }

    public void createLuck(){
        LinkedHashMap<String,Object> map = new LinkedHashMap<>();
        for (LuckType type:LuckType.values()){
            map.put(type.getName(),type.getType());
        }
        this.createConfig(map);

    }

    static LinkedHashMap<chestType,LinkedList<GiveItemAndCommands>> getChestItems(String name){
        if(!isNotNull(name))
            return null;
        Config config = getLuckConfig(name);
        if(config != null){
            LinkedHashMap<chestType,LinkedList<GiveItemAndCommands>> items = new LinkedHashMap<>();
            List list_remove = config.getList(LuckType.costItems.getName());
            List list_add = config.getList(LuckType.reward.getName());
            LinkedList<GiveItemAndCommands> add = new LinkedList<>(),
                                            remove = new LinkedList<>();
            for (Object s:list_remove){
                String string = (String) s;
                remove.add(decomposeString(string,chestType.Cost));
            }
            items.put(chestType.Cost,remove);
            for (Object s:list_add) {
                String string = (String) s;
                add.add(decomposeString(string,chestType.reward));
                items.put(chestType.reward,add);
            }
            items.put(chestType.reward,add);
            return items;
        }
        return null;
    }
    private static GiveItemAndCommands decomposeString(String string,chestType type){
        String[] MenuItem =  string.split("&");
        switch (type){
            case Cost:
                if(MenuItem.length > 1){
                    switch (MenuItem[1]){
                        case "money":
                            String message = "金币";
                            if(MenuItem.length > 2){
                                message = MenuItem[2];
                            }
                            return new GiveItemAndCommands(Integer.parseInt(MenuItem[0]),MenuItem[0]+" "+message);

                        case "item":
                            String itemString = MenuItem[0];
                            String[] itemArray = itemString.split(":");
                            Item item = null;
                            message = "物品";
                            if(MenuItem.length > 2){
                                message = MenuItem[2];
                            }
                            if(itemArray.length > 2){
                                item = new Item(Integer.parseInt(itemArray[0]),Integer.parseInt(itemArray[1]),Integer.parseInt(itemArray[2]));
                            }else if(itemArray.length > 1){
                                item = new Item(Integer.parseInt(itemArray[0]),Integer.parseInt(itemArray[1]),1);
                            }else if(itemArray.length == 1){
                                item = new Item(Integer.parseInt(itemArray[0]),0,1);
                            }
                            if(item != null){
                                return new GiveItemAndCommands(item,message);
                            }

                            break;
                    }

                }
                break;
            case reward:
                if(MenuItem.length >= 1){
                    String message = "这是一条指令";
                    int random = 100;
                    if(MenuItem.length > 2){
                        message = MenuItem[2];
                    }
                    if(MenuItem.length > 1){
                        random = Integer.parseInt(MenuItem[1]);
                    }
                    return new GiveItemAndCommands(MenuItem[0],message,random);
                }
                break;
        }
        return null;
    }

    /**
     * @return 获取所有抽奖箱的文件名
     * */
    static LinkedList<String> getLucks(){
        LinkedList<String> list = new LinkedList<>();
        File file = new File(Luck.getLuck().getDataFolder()+"/LuckDraw");
        File[] files = file.listFiles();
        if(files != null){
            for (File file1:files){
                if(file1.isFile()){
                    String fileName = file1.getName();
                    int dot = fileName.lastIndexOf('.');
                    if ((dot >-1) && (dot < (fileName.length()))) {
                        fileName = fileName.substring(0, dot);
                    }
                    list.add(fileName);

                }
            }
            return list;
        }
        return null;
    }
    static LinkedList<String> getLuckNames(){
        LinkedList<String> configs = getLucks();
        LinkedList<String> names = new LinkedList<>();
        if(configs != null){
            for (String name :configs){
                Config config = getLuckConfig(name);
                if(config != null){
                    names.add(config.getString(LuckType.chestName.getName()));
                }
            }
        }else{
            return null;
        }
        return names;
    }

    public enum LuckType{
        chestName("chest-name","测试抽奖箱"),
        costItems("cost-removes",new ArrayList<String>(){
            {
                add("5000&money&金币");
                add("1:0:1&item&石头 * 1");
                add("5:0:1&item&木板 * 1");
            }
        }),
        reward("reward",new ArrayList<String>(){
            {
                add("give @p 264:0 1&20&钻石 * 1");
                add("kill @p&40&自杀");
            }
        });

        protected String name;
        protected Object type;
        LuckType(String name,Object defaultType){
            this.name = name;
            this.type = defaultType;
        }

        public String getName() {
            return name;
        }

        public Object getType() {
            return type;
        }
    }
    public enum chestType{
        Cost,reward
    }


}
class GiveItemAndCommands{

    private Item item = null;
    private int money = 0;
    private int random = 100;
    private String command = null;
    private String message;
    GiveItemAndCommands(Item item, String message){
        this.item = item;
        this.message = message;
    }

    GiveItemAndCommands(int money, String message){
        this.money = money;
        this.message = message;
    }

    String getMessage() {
        return message;
    }

    GiveItemAndCommands(String command, String message, int random){
        this.command = command;
        this.random = random;
        this.message = message;
    }

    int getRandom() {
        return random;
    }

    Item getItem() {
        return item;
    }

    int getMoney() {
        return money;
    }

    String getCommand() {
        return command;
    }
}
