// K.REAPER PROJEKT — Character Database
const CHARACTERS = {
  kryss: {
    id: "kryss",
    portrait: "assets/kryss_reaper.jpg",
    name: "KRYSS REAPER",
    title: "WASTELAND EXECUTIONER",
    gender: "male",
    style: "Brutal close-range",
    special: "Shadow Finishers",
    weapon: "Improvised + Bone",
    bio: "Dark. Sarcastic. Unpredictable. Heavy strikes, supernatural edges, and finishers that leave nothing standing.",
    colors: { primary: "#c41e3a", accent: "#39ff14" },
    unlockable: false,
    progression: { level: 1, abilities: ["Heavy Strike", "Bone Cleave"] }
  },
  nyxzara: {
    id: "nyxzara",
    portrait: "assets/nyxzara_voss.jpg",
    name: "NYXZARA VOSS",
    title: "SHADOW RAZOR",
    gender: "female",
    style: "Fast / Evasive",
    special: "Green Energy",
    weapon: "Precision + Laser",
    bio: "Confident. Dangerous. Intelligent. Acrobatics, precision strikes, and toxic green energy abilities.",
    colors: { primary: "#39ff14", accent: "#c41e3a" },
    unlockable: false,
    progression: { level: 1, abilities: ["Laser Lash", "Shadow Step"] }
  }
};
const ROSTER_EXPANDABLE = ["hack", "chernobyl", "night_mary", "severin", "virgin_mary", "blonde_nun"];
window.CHARACTERS = CHARACTERS;
window.ROSTER_EXPANDABLE = ROSTER_EXPANDABLE;