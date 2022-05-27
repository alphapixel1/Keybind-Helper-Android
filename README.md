# Keybind Helper 

---

Design Document  

Nick Bell
Eddie Greenlee
Maximiliano De Santiago Galan
Blake Barr
Richard Woessner

## Introduction 
You open a game but can't remember all the stupid keybinds for the game and have to do the clicks of shame 
This app allows you to
-	Save Keybinds
-	Put Keybinds into groups
-	Organzie the keybinds
-	Be aware of upcoming events for a plant: when to water, when growing season ends, etc.  


## Storyboard

[Plant Diary Storyboard](https://projects.invisionapp.com/prototype/Plant-Diary-ck0bict0n005bqh01aaeu8tuu/play/c6560121)

(https:![Keybind Veiwer Class](https://user-images.githubusercontent.com/23407049/170616672-c83835a2-006d-4fa4-bed5-97d8b30d9a11.png)

## Functional Requirements

### Requirement 100.0: Save Keybinds

#### Scenario

As a user interested in games, I want to be able to save my set keybinds so that I dont have to open settings and can look at it while playing the game

#### Dependencies

User has a game that has keybinds

#### Assumptions

The user is capable of using an app

#### Examples
1.1
**Given** None

**When** Add group button is clicked

**Then** A group with a unique name in the "Game Project" will be created and displayed



1.2  
**Given** a created set of keybinds for a specific game

**When** the user searches up a game in the open game menu

**Then** I should receive at least a result if the game has been made by the user
Game: Ready or Not


### Requirement 101: Create a Keybind

#### Scenario

A user wants to add a keybind to a group

#### Dependencies

None

#### Assumptions  

None

#### Examples  

1.1  
**Given** A keybind from a game
**When**  I click the add button on the group

**Then** a new keybind prompt will appear

Group name: Movement

Keybind Name: Forward  
Keybind 1: W
Keybind 2: [blank]
Keybind 3: [blank]

### Requirement 102: Move Keybinds

#### Scenario
A user wants to move keybinds between groups

### Dependancies

There is atleast 1 or 2 groups

### Assumptions

None

### Examples

1.1
**Given** Two Groups

**When**  I click the dropdown on Group A and click Merge into Group B

**Then** All the keybinds in Group A will be added to Group B and Group A will be deleted

1.2
**Given** One Group and 2 Keybinds

**When** A keybind is Pressed and held for .5-1 second

**Then** A menu will appear that has up and down buttons to move the keybind in that direction in that group

### Requirement 103: Edit Keybinds
#### Scenario

A user wants to edit keybinds

###Dependancies

There is atleast 1 group

###Assumptions

None

### Examples

1.1

**Given** A keybind

**When** Keybind is clicked

**Then** A Menu to edit the keybinds name, keybind 1, keybind 2, keybind 3 is displayed

1.2
**Given** Keybind edit menu has a valid unique name

**When** User clicks done

**Then** Keybind card is updated


### Requirement 104: Save Key Binds for Multiple Games

#### Scenario
As a gamer who plays multiple games, I want to be able to save key binds for multiple games at once

#### Dependencies
The set of key binds has been named.
Key binds have been added by the user.

#### Assumptions
The user has chosen a game and wishes to save the key binds

#### Example
1.1

*Given* The user has created a set of key binds for a game and named it
*When* The user is finished adding new key binds and hits save
*Then* The set of key binds are saved and can be viewed anytime

## Class Diagram

![ClassDiagram](https://user-images.githubusercontent.com/2224876/82162015-54387780-986f-11ea-998f-a45fdf8c3bf1.png)

### Class Diagram Description


**MainActivity:**  The first screen the user sees.  This will have a blank group or that last saved Game Keybinds

**EditKeybindActivity:**  A screen that allows you to input new data to the keybind and modify it

**OpenGameFragment** A fragment that allows you to switch between games

**SettingsFragment** A Fragment that allows you to change the theme/colors

**Group** Class that represents a group and has a name and an arraylist of Keybinds

**Keybind:** A class with 4 strings: Name, Keybind 1, Keybind 2, Keybind 3



## Scrum Roles

- DevOps/Product Owner/Scrum Master: Nick Bell  
- Frontend Developer: Blake Barr  
- Integration Developer: Max
- Room Database Developer: Eddie  
- Testing: Richard

## Weekly Meeting

Any day after 5pm On Discord
