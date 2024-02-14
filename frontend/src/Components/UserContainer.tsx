import { FC } from "react";
import { User } from "../DTO/User";
import { UserBox } from "./UserBox";

const UserContainer: FC<{ users: User[]; search: string }> = ({
  users,
  search,
}) => {
  function filter(users: User[]): User[] {
    if (search.trim() === "") return users;
    return users.filter((user) => user.name.includes(search));
  }

  return (
    <>
      <div className="users-container">
        {filter(users).map((user, index) => (
          <UserBox key={index} {...user} />
        ))}
      </div>
    </>
  );
};

export default UserContainer;
