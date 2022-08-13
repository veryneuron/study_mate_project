export default class connData {
  userId: string;
  type: 'Connected' | 'Disconnected' | 'Unauthorized' | '';
  constructor(userID = '', type = '') {
    this.userId ??= userID;
    if (
      type === 'Connected' ||
      type === 'Disconnected' ||
      type === 'Unauthorized' ||
      type === ''
    ) {
      this.type = type;
    } else {
      this.type = '';
    }
  }
}
