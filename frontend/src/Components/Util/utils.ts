export const isBlank = (value?: string) => {
  return value === undefined || value.trim().length === 0;
};

export const isNotBlank = (value?: string) => {
  return !isBlank(value);
};
