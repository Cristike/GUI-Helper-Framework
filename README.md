# GHF
*GHF (GUI Helper Framework) is a light framework that is meant to help in the creation of GUIs.*

*This framework works for version 1.9.x or newer.*

# Maven Dependency
*In order to add the framework as a dependency, you'll firstly need to add the JitPack repository to your pom.xml:*

```xml
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
```

*Next, add the dependency to your pom.xml:*

```xml
  <dependency>
      <groupId>com.github.Cristike</groupId>
      <artifactId>GUI-Helper-Framework</artifactId>
      <version>VERSION</version>
  </dependency>
```

# Usage
*GHF makes the creation of GUIs really easy. Most of the functionality lies in the [Gui.java](https://github.com/Cristike/GHF/blob/master/src/main/java/me/cristike/ghf/Gui.java) class*.  
*First thing first, we start by creating a new Gui object:*

```java
  Gui gui = new Gui(plugin, "example", "Example GUI", 54);
```
*Where **plugin** is the plugin we develop, **"example"** is the GUI's id, **"Example GUI"** is the GUI's title and **45** is the size.*

*Now we can start adding elements to the GUI:*  
```java
  ItemStack fillItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);

  gui.fillLine(fillItem, 0);
  gui.fillLine(fillItem, 5);
  gui.fillColumnInRange(fillItem, 0, 1, 4);
  gui.fillColumnInRange(fillItem, 8, 1, 4);
```  
*Here we create an outer rim of black glass panes using the functions
[fillLine()](https://github.com/Cristike/GHF/blob/c4e6974047d7beae1409ad79d679007d5e2b9565/src/main/java/me/cristike/ghf/Gui.java#L265) and
[fillColumnInRange()](https://github.com/Cristike/GHF/blob/c4e6974047d7beae1409ad79d679007d5e2b9565/src/main/java/me/cristike/ghf/Gui.java#L320)*

*Lastly we need to update the inventory and open it to a player p:*
```java
  gui.update();
  gui.open(p);
```
*The final result looks something like this:*  
![](https://cdn.discordapp.com/attachments/957707270228168835/984133778723176508/example.png)

# Actions
*Another important feature of any GUI is functionality. GHF allows it's users to assign a GuiAction to any slot. (Currently only click actions are supported)*

*To get started, we'll add a green glass pane in the middle of the GUI*
```java
  gui.set(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), 22);
```  
*Now we can assign an action to this button. We will do a simple instruction: sending a message.*
```java
  gui.registerAction(new GuiAction() {
      @Override
          public void executeClickAction(InventoryClickEvent event) {
              event.getWhoClicked().sendMessage("GuiAction executed!");
      }
  }, 22);
```  
*With this action added, every time we will click on the green glass, "GuiAction executed!" will be sent back via the chat.*
