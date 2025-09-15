import {Identify} from "./components/Identify.tsx";
import {CreatePoll} from "./components/CreatePoll.tsx";
import {ListPolls} from "./components/ListPolls.tsx";
import {createUser, type User} from "./services/api.ts";
import {useEffect, useState} from "react";

type view = "identify" | "menu" | "polls" | "createPoll";

function App() {
    const [user, setUser] = useState<User | null>(null);
    const [view, setView] = useState<view>("identify");

    //Download user from localStorage
    useEffect(() => {
        const raw = localStorage.getItem("user");
        if (raw) setUser(JSON.parse(raw));
        setView(raw ? "menu" : "identify")
    }, [])

    //Save to localstorge if user changes
    useEffect(() => {
        if (user) localStorage.setItem("user", JSON.stringify(user));
        else localStorage.removeItem("user");
    }, [user]);

    async function handleLogin(username: string, email:string) {
        const u = await createUser(username, email);
        setUser(u);
        setView("menu")
    }
    function logout() {
        setUser(null);
        setView("identify");
    }

  return (
    <>
        <div className={"p-4 max-w-2xl mx-auto space-y-4"}>
            {view === "identify" && <Identify onLogin={handleLogin} />}

            {view === "menu" && user && (
                <div className={"space-y-3"}>
                    <div>Logged in as <b>{user.username}</b> ({user.email})</div>

                    <div className={"flex gap-2"}>
                        <button className={"bg-emerald-800 text-amber-100 px-3 py-2 rounded"} onClick={() => setView("createPoll")}>
                            Create new poll
                        </button>

                    </div>

                    <button className={"bg-emerald-800 text-amber-100 px-3 py-2 rounded"} onClick={() => setView("polls")}>
                        Available polls
                    </button>

                    <button className={"text-sm underline ml-auto"} onClick={logout}>
                        Logout
                    </button>
                </div>
            )}

            {view === "createPoll" && user && (
                <CreatePoll creatorId={user.id} onDone={() => setView("menu")}/>
            )}

            {view === "polls" && user && (
                <ListPolls currentUserId={user.id} onBack={() => setView("menu")}/>
            )}
        </div>
    </>
  )
}

export default App
