package LuckDraw.utils;


import LuckDraw.Luck;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import com.google.gson.GsonBuilder;
import me.onebone.economyapi.EconomyAPI;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class createUI {
    static final int menu = 0xacea11;
    static final int click = 0xacea12;
    private int id;
    private LinkedHashMap<String,Object> data;
    public static createUI getAPI(){
        return new createUI();
    }

    private void send(Player player){
        ModalFormRequestPacket ui = new ModalFormRequestPacket();
        ui.formId = this.id;
        ui.data = new GsonBuilder().setPrettyPrinting().create().toJson(data);
        player.dataPacket(ui);
    }

    public void sendMenuUI(Player player){
        this.id = menu;
        ArrayList<LinkedHashMap<String,Object>> button = new ArrayList<>();
        LinkedList<String> names = LuckFile.getLuckNames();
        if(names != null){
            for (String name:names){
                LinkedHashMap<String,Object> buttons = new LinkedHashMap<>(),
                                             image   = new LinkedHashMap<>();
                buttons.put("text",name);
                image.put("type","path");
                image.put("data","textures/ui/gift_square");
                buttons.put("image",image);
                button.add(buttons);
            }
        }
        LinkedHashMap<String,Object> data = new LinkedHashMap<>();
        data.put("type","form");
        data.put("title","抽奖箱");
        data.put("content","");
        data.put("buttons",button);
        this.data = data;
        send(player);
    }

    void sendClickUI(Player player){
        if(Luck.click.containsKey(player)){
            this.id = click;
            StringBuilder stringBuilder = new StringBuilder("");
            LinkedHashMap<LuckFile.chestType,LinkedList<GiveItemAndCommands>>
                    getting = LuckFile.getChestItems(Luck.click.get(player));
            if(getting != null){
                LinkedList<GiveItemAndCommands> remove = getting.get(LuckFile.chestType.Cost);
                LinkedList<GiveItemAndCommands> add = getting.get(LuckFile.chestType.reward);
                if(remove != null){
                    stringBuilder.append("§e============================\n");
                    for (GiveItemAndCommands command2:remove){
                        boolean bool = canRemove(player,command2);
                        if(bool){
                            stringBuilder.append("§b 消耗: §a").append(command2.getMessage()).append("  (充足)").append("\n");
                        }else{
                            stringBuilder.append("§b 消耗: §c").append(command2.getMessage()).append("  (缺失)").append("\n");
                        }
                    }
                    stringBuilder.append("§e============================\n");
                }
                if(add != null){
                    stringBuilder.append("§d============================\n");
                    for (GiveItemAndCommands commands:add){
                        stringBuilder.append(getRandomString(commands.getRandom())).append(": §2").append(commands.getMessage()).append("\n");
                    }
                    stringBuilder.append("§d============================\n\n");
                }

            }
            ArrayList<LinkedHashMap<String,Object>> button = new ArrayList<>();

            LinkedHashMap<String,Object> buttons1 = new LinkedHashMap<>(),
                                         image1   = new LinkedHashMap<>(),
                                         buttons2 = new LinkedHashMap<>(),
                                         image2   = new LinkedHashMap<>();

            buttons1.put("text","开始抽奖!!");
            image1.put("type","path");
            image1.put("data","textures/ui/gift_square");
            buttons1.put("image",image1);
            button.add(buttons1);
            buttons2.put("text","返回上一级!!");
            image2.put("type","path");
            image2.put("data","textures/ui/arrow_l_hover");
            buttons2.put("image",image2);
            button.add(buttons2);
            String s = stringBuilder.toString();
            if(s.equals("")){
                s = "什么都没有~~~";
            }
            LinkedHashMap<String,Object> data = new LinkedHashMap<>();
            data.put("type","form");
            data.put("title","抽奖箱");
            data.put("content",s);
            data.put("buttons",button);
            this.data = data;
            send(player);
        }else{
            player.sendMessage("出现未知错误，请重新选择抽奖箱");
        }


    }
    private String getRandomString(int random){
        if(random == 100){
            return "§d必定获得";
        } else if(random >= 60 && random < 100){
            return "§a较大几率获得";
        }else if(random >= 10 && random <60){
            return "§6较小几率获得";
        }else{
            return "§c极小几率获得";
        }
    }

    private boolean canRemove(Player player,GiveItemAndCommands commands){
        if(commands.getItem() != null){
            for(Item item:player.getInventory().getContents().values()){
                if(item.equals(commands.getItem(),true,false)){
                    if(commands.getItem().getCount() <= item.getCount()){
                        return true;
                    }
                }
            }
        }
        if(commands.getMoney() > 0){
            if(Server.getInstance().getPluginManager().getPlugin("EconomyAPI") != null){
                double playerMoney = EconomyAPI.getInstance().myMoney(player);
                if(playerMoney >= commands.getMoney()){
                    return true;
                }
            }
        }
        return false;
    }



}
