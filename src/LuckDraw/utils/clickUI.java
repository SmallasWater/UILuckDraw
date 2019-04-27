package LuckDraw.utils;


import LuckDraw.Luck;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import me.onebone.economyapi.EconomyAPI;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;


public class clickUI implements Listener{

    @EventHandler
    public void getUI(DataPacketReceiveEvent event){
        String data;
        ModalFormResponsePacket ui;
        Player player = event.getPlayer();
        if(!(event.getPacket() instanceof ModalFormResponsePacket)) return;
        ui = (ModalFormResponsePacket)event.getPacket();
        data = ui.data.trim();
        int fromId = ui.formId;
        switch (fromId){
            case createUI.menu:
                if(!data.equals("null")){
                    LinkedList<String> strings = LuckFile.getLucks();
                    if(strings != null){
                        Luck.click.put(player,strings.get(Integer.parseInt(data)));
                        createUI.getAPI().sendClickUI(player);
                    }
                }
                break;
            case createUI.click:
                if(!data.equals("null")){
                    if(Integer.parseInt(data) == 0){
                        LinkedHashMap<LuckFile.chestType, LinkedList<GiveItemAndCommands>>
                                Items = LuckFile.getChestItems(Luck.click.get(player));
                        if(Items != null){
                            if(Items.containsKey(LuckFile.chestType.Cost)){
                                int money = 0;
                                LinkedList<Item> items = new LinkedList<>();
                                LinkedList<GiveItemAndCommands> remove = Items.get(LuckFile.chestType.Cost);
                                LinkedList<GiveItemAndCommands> add = Items.get(LuckFile.chestType.reward);
                                for(GiveItemAndCommands commands:remove){
                                    money += commands.getMoney();
                                    if(commands.getItem() != null)
                                        items.add(commands.getItem());
                                }
                                if(Server.getInstance().getPluginManager().getPlugin("EconomyAPI") != null){
                                    if(money > EconomyAPI.getInstance().myMoney(player)){
                                        player.sendMessage("§b§l§o[抽奖]§c 抱歉，您的金钱不足");
                                        return;
                                    }else{
                                        EconomyAPI.getInstance().reduceMoney(player,money);
                                    }
                                }
                                if(!items.isEmpty()){
                                    if(isMathInventory(player,items)){
                                        for(Item removes:items){
                                            player.getInventory().removeItem(removes);
                                        }
                                    }else{
                                        player.sendMessage("§b§l§o[抽奖]§c 抱歉 您的材料不足");
                                        return;
                                    }
                                }
                                    ////开始抽奖//////
                                int c = 0;
                                for(GiveItemAndCommands commands:add){
                                    int random = new Random().nextInt(100);
                                    if(commands.getRandom() >= random){
                                        player.sendMessage("§b§l§o[抽奖]§a 恭喜你获得 "+commands.getMessage());
                                        if(player.isOp()){
                                            Server.getInstance().dispatchCommand(player, (commands.getCommand().replace("@p",player.getName())));
                                        }else{
                                            Server.getInstance().addOp(player.getName());
                                            Server.getInstance().dispatchCommand(player, (commands.getCommand().replace("@p",player.getName())));
                                            Server.getInstance().removeOp(player.getName());
                                        }

                                        c++;
                                    }
                                }
                                if(c == 0){
                                    player.sendMessage("§b§l§o[抽奖] §c抱歉~~ 你什么都没获得 ");
                                }
                            }

                        }else{
                            player.sendMessage("§b§l§o[抽奖] §c抱歉 抽奖箱没有任何物品");
                        }
                    }else if(Integer.parseInt(data) == 1){
                        createUI.getAPI().sendMenuUI(player);
                    }
                }
                break;

        }

    }

    private boolean isMathInventory(Player player,LinkedList<Item> inventory){
        if(inventory == null) return false;
        for(Item item:inventory){
            Item playerHave = playerHasItem(player,item);
            if(playerHave != null){
                return item.getCount() <= playerHave.getCount();
            }
        }
        return false;
    }
    private Item playerHasItem(Player player,Item item){
        for (Item item1:player.getInventory().getContents().values()){
            if(item1.equals(item,true,false))
                return item1;
        }
        return null;
    }
}
