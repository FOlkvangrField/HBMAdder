package ha;

import com.hbm.util.ChatBuilder;
import ha.Converter.TransferRecipe;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import static ha.Loader.loadRecipeFromJson;

public class CommandReloadTransferRecipes extends CommandBase {
    @Override
    public String getCommandName() {
        return "hareload";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hareload";
    }
    @Override
    public void processCommand(ICommandSender sender, String[] args){
        try {
            TransferRecipe.rollBack();
            loadRecipeFromJson(true);
            TransferRecipe.Construct();
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Reload complete :)"));
        } catch(Exception ex){
            sender.addChatMessage(ChatBuilder.start("----------------------------------").color(EnumChatFormatting.GRAY).flush());
            sender.addChatMessage(ChatBuilder.start("An error has occoured during loading, consult the log for details.").color(EnumChatFormatting.RED).flush());
            sender.addChatMessage(ChatBuilder.start(ex.getLocalizedMessage()).color(EnumChatFormatting.RED).flush());
            sender.addChatMessage(ChatBuilder.start(ex.getStackTrace()[0].toString()).color(EnumChatFormatting.RED).flush());
            sender.addChatMessage(ChatBuilder.start("----------------------------------").color(EnumChatFormatting.GRAY).flush());
            throw ex;
        }

    }

}
