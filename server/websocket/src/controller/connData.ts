export default class connData {
  userId: string;
  type:
    | 'Connected'
    | 'Disconnected'
    | 'Unauthorized'
    | 'StudyTime'
    | 'StudyRecord'
    | '';
  constructor(userID = '', type = '') {
    this.userId ??= userID;
    if (
      type === 'Connected' ||
      type === 'Disconnected' ||
      type === 'Unauthorized' ||
      type === 'StudyTime' ||
      type === 'StudyRecord' ||
      type === ''
    ) {
      this.type = type;
    } else {
      this.type = '';
    }
  }
}
