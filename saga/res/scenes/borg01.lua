local hero = getHero(0).getName()

speak("BORG", hero .. ", so you're almost ready to go!")
speak(hero, "I guess so. We're off to find Jonas and Janine... and the lost city of Hero.")
speak("BORG", "I don't care about Hero, but I miss the twins. I remember the day they left.")

fade('black')
teleport('world1/parish.tmx', 19, 28, 'SOUTH', false)
sceneSwitch('opening_flashback', true)
hideHero(true)
fade('normal')

speak(" -- Five years ago -- ")
wait(.7)
speak("Jonas", "Goodbye, BORG!")
speak("Janine", "Thanks for taking care of us all these years.")
walk("elder", 1, "SOUTH", true)
speak("Elder", "The lost city of Hero... Maybe you two will can find a way back in. I'd love to see the traders from the other worlds again.")
speak("Jonas", "Maybe we'll even find another robot like BORG!")
walk("clone", 8, "EAST", true)
speak(hero, "Wait! You can't leave! Nobody's ever come back from Hero!")
speak("Janine", hero .. "... We'll miss you.")
speak("Jonas", "We'll be back, I promise. When you grow up you can come exploring with us.")
speak("Janine", "Goodbye " .. hero .. "!");
face("clone", "SOUTH")
walk("jonas", 10, "SOUTH", false)
walk("janine", 10, "SOUTH", true);

fade('black')
teleport('world1/parish_interior.tmx', 24, 8, 'EAST', false)
sceneSwitch('opening_flashback', false)
hideHero(false)
face('borg', 'WEST')
fade('normal')

speak(hero, "I want to find out what happened to them. Maybe Jonas and Janine found their way into Hero.")
speak("BORG", "If find any hint of them, you come back and tell me, okay? And be careful!")
speak(hero, "I will.")
speak("BORG", "Oh, Elder wanted to talk to you before you left.")
sceneSwitch('opening_talked_borg', true)
