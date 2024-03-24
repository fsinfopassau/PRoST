import { useEffect, useState } from "react";
import { User } from "../../Types/User";
import { getAllUsers } from "../../Queries";
import { UserSettingCard } from "./UserSettingCard";

export function UserSettings() {
    const [users, setUsers] = useState<User[]>([]);

    useEffect(reloadUsers, []);

    function reloadUsers() {
        getAllUsers().then((users) => {
            if (users) setUsers(users);
        });
    }

    return <>
        <div className="CardContainer">
            {users.map((user, index) => (
                <UserSettingCard user={user} key={index} onUpdate={reloadUsers} />
            ))}
        </div>
    </>
}