# ExceptionsInCode
This is Intellij IDEA plugin developed for showing exceptions containing issues from bugtracking systems right in code, at the apropriate file.

## Currently implemented features
* managing sources of issues
* list of all downloaded issues, jump to code place by clicking on item
* draggable and dropable, copyable notifications in the gutter area, open issue description by clicking an icon
* issue showing window with ability to jump right to YouTrack

## Building and running
1. Clone this repo
3. In IDEA Platform repo, reset to revision i was working with:
`git reset --hard bbea9fb5ce77eb706986b386292c5371743b88b7`
You may don`t do it, but if you do, it will definitely work well. 
3. In IDEA go to Project Structure (Ctrl+Alt+Shift+S), select Project SDK -> New -> Intellij Platform Plugin SDK -> Path to folder where IDEA has been installed
4. Edit configurations -> Add -> Plugin -> Ok
5. Click Run

