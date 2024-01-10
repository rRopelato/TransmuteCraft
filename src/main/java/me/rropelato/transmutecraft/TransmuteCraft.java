package me.rropelato.transmutecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import java.util.Random;

public final class TransmuteCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        // Registrar o comando /transmute
        getCommand("transmute").setExecutor(new TransmuteCommand());

        // Log de start
        System.out.println("TransmuteCraft has started.");
    }

    @Override
    public void onDisable() {
        // Log de end
        System.out.println("TransmuteCraft has ended.");
    }
}

class TransmuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar se o comando foi executado por um jogador
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando só pode ser executado por jogadores.");
            return true;
        }

        Player player = (Player) sender;

        // É pra criar uma grid, mas talvez não esteja funcionando
        Inventory transmuteInventory = player.getInventory();

        // Linka o inventario a quem usou o comando de forma direta
        realizarTransmutacao(player, transmuteInventory);

        return true;
    }

    // Codigo pra fazer a transmutação acontecer
    private void realizarTransmutacao(Player player, Inventory transmuteInventory) {
        // Transmuta e cria log no cmd
        int rawOreCount = contarRawOres(transmuteInventory);
        int coalCount = contarCarvao(transmuteInventory);

        System.out.println("Raw Ore Count: " + rawOreCount);
        System.out.println("Coal Count: " + coalCount);

        // Verificar se há ingredientes suficientes para a transmutação
        if (rawOreCount >= 8 && coalCount >= 1) {
            // Remover os ingredientes do inventário
            removerItens(transmuteInventory, Material.RAW_IRON, 8);
            removerItens(transmuteInventory, Material.COAL, 1);

            // Calcular a chance de duplicação (10%)
            if (chanceDuplicacao()) {
                // Se a duplicação ocorrer, adicionar duas barras ao inventário do jogador
                ItemStack resultadoDuplicado = new ItemStack(Material.IRON_INGOT, 2);
                player.getInventory().addItem(resultadoDuplicado);

                player.sendMessage("Transmutação de Raw Ore para Iron Ingot concluída com sucesso! (Duplicado)");
            } else {
                // Adicionar o resultado (duplicado ou não) ao inventário do jogador
                ItemStack resultado = new ItemStack(Material.IRON_INGOT, 1 + new Random().nextInt(2));
                player.getInventory().addItem(resultado);

                player.sendMessage("Transmutação de Raw Ore para Iron Ingot concluída com sucesso!");
            }
        } else {
            player.sendMessage("Você não tem ingredientes suficientes para a transmutação.");
            }
    }

    // Calculo da % pra duplicar
    private boolean chanceDuplicacao() {
        return new Random().nextInt(10) == 0;
    }

    // Contador de item no inventario
    private int contarItens(Inventory inventory, Material material) {
        int count = 0;
        ItemStack[] contents = inventory.getContents();

        for (ItemStack item : contents) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }

        return count;
    }

    // Codigo pra remover do inv do jogador os materiais
    private void removerItens(Inventory inventory, Material material, int quantidade) {
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                int quantidadeRemovida = Math.min(item.getAmount(), quantidade);
                item.setAmount(item.getAmount() - quantidadeRemovida);
                quantidade -= quantidadeRemovida;

                if (quantidade <= 0) {
                    break;
                }
            }
        }
    }

    // Código para contar a quantidade de raw ore no inventario
    private int contarRawOres(Inventory inventory) {
        return contarItens(inventory, Material.RAW_IRON);
    }

    // Código para contar a quantidade de carvão no inventario
    private int contarCarvao(Inventory inventory) {
        return contarItens(inventory, Material.COAL) + contarItens(inventory, Material.CHARCOAL);
    }

}
