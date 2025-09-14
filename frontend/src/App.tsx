import {Identify} from "./components/Identify.tsx";
import {CreatePoll} from "./components/CreatePoll.tsx";
import {ListPolls} from "./components/ListPolls.tsx";

function App() {


  return (
    <>
        <Identify/>
        <CreatePoll />

        <div className={"p-4 max-w-2xl mx-auto"}>
            <h1 className={"text-2xl font-bold mb-4"}>
                Available polls
            </h1>
            <ListPolls />
        </div>

    </>
  )
}

export default App
