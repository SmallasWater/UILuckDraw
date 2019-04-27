package LuckDraw;


import LuckDraw.utils.LuckFile;
import LuckDraw.utils.clickUI;
import LuckDraw.utils.createUI;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;



import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author 若水
 * <p> 本插件由YUIO定制 未经作者允许 严禁从事商业活动</>
 * */
public class Luck extends PluginBase{
    private static Luck luck;
    public static LinkedHashMap<Player,String> click = new LinkedHashMap<>();

    public void onEnable(){
        luck = this;
        File file = new File(this.getDataFolder()+"/LuckDraw");
        if(!file.exists())
            if(!file.mkdirs()){
                Server.getInstance().getLogger().warning("文件夹创建失败");
            }
        this.getServer().getPluginManager().registerEvents(new clickUI(),this);
    }

    public static Luck getLuck() {
        return luck;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.isOp()){
            if(args.length > 1){
                   if(args[0].equals("add")){
                       LuckFile.getFile(args[1]).createLuck();
                        sender.sendMessage("抽奖箱创建成功");
                   }
            }else{
                if(sender instanceof Player)
                    createUI.getAPI().sendMenuUI((Player) sender);
            }
        }else{
            return false;
        }
        return true;
    }
}
