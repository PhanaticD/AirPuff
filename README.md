# AirPuff
Basic Tutorial ProjectKorra Addon
airbender move that shoots a cloud out from you that does 1 damage

this is meant to be a tutorial to get started with working with ProjectKorra addons, pretty much everything has comments explaining what they do

here is the general flow of an addon:
when starting the server, ProjectKorra calls load() in the addon. load() registers a listener for player clicks. when a player clicks it runs AirPuff(), which then runs start() which tells PK to run progess() every tick. progress() eventually calls remove()

I recommend using IntelliJ IDEA community edition
to clone this project, click File>New>Project from Version Control>git and then put the github URL that shows up when you click the green "clone or download" button

if importing and using this project as a maven project, you DO need to also clone and compile the 1.13 branch of ProjectKorra once in order for it to be installed on your computers local maven repository so it can be used as a dependency
