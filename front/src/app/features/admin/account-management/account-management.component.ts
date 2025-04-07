import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { AccountService } from '../../../services/account.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-account-management',
  templateUrl: './account-management.component.html',
  styleUrls: ['./account-management.component.css']
})
export class AccountManagementComponent implements OnInit {
  accounts: any[] = [];
  isLoading = false;
  isModalVisible = false;
  accountForm!: FormGroup;
  isEditMode = false;
  currentAccountId: number | null = null;

  accountTypes = [
    { label: 'Checking', value: 'CHECKING' },
    { label: 'Savings', value: 'SAVINGS' },
    { label: 'Investment', value: 'INVESTMENT' },
    { label: 'Loan', value: 'LOAN' }
  ];

  hrManagers: any[] = [];
  intermediaryClients: any[] = [];

  formatterDollar = (value: number): string => `$ ${value}`;
  parserDollar = (value: string): string => value.replace('$ ', '');

  constructor(
    private accountService: AccountService,
    private userService: UserService,
    private fb: FormBuilder,
    private message: NzMessageService,
    private modal: NzModalService
  ) {}

  ngOnInit(): void {
    this.loadAccounts();
    this.loadHRManagers();
    this.loadIntermediaryClients();
    this.initForm();
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.accountService.getAllAccounts().subscribe({
      next: (data) => {
        this.accounts = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.message.error('Failed to load accounts');
        this.isLoading = false;
        console.error('Error loading accounts:', error);
      }
    });
  }

  loadHRManagers(): void {
    this.userService.getHRManagers().subscribe({
      next: (data) => {
        this.hrManagers = data;
      },
      error: (error) => {
        this.message.error('Failed to load HR managers');
        console.error('Error loading HR managers:', error);
      }
    });
  }

  loadIntermediaryClients(): void {
    this.userService.getIntermediaryClients().subscribe({
      next: (data) => {
        this.intermediaryClients = data;
      },
      error: (error) => {
        this.message.error('Failed to load clients');
        console.error('Error loading clients:', error);
      }
    });
  }

  initForm(): void {
    this.accountForm = this.fb.group({
      accountNumber: [null, [Validators.required]],
      accountName: [null, [Validators.required]],
      accountType: ['CHECKING', [Validators.required]],
      balance: [0, [Validators.required, Validators.min(0)]],
      active: [true],
      hrManagerId: [null, [Validators.required]],
      intermediaryClientId: [null, [Validators.required]]
    });
  }

  showModal(account?: any): void {
    this.isEditMode = !!account;

    if (account) {
      this.currentAccountId = account.id;
      this.accountForm.patchValue({
        accountNumber: account.accountNumber,
        accountName: account.accountName,
        accountType: account.accountType,
        balance: account.balance,
        active: account.active,
        hrManagerId: account.hrManagerId,
        intermediaryClientId: account.intermediaryClientId
      });
    } else {
      this.currentAccountId = null;
      this.accountForm.reset({
        active: true,
        accountType: 'CHECKING',
        balance: 0
      });
    }

    this.isModalVisible = true;
  }

  submitForm(): void {
    if (this.accountForm.valid) {
      const accountData = this.accountForm.value;

      if (this.isEditMode && this.currentAccountId) {
        // Update account
        this.accountService.updateAccount(this.currentAccountId, accountData).subscribe({
          next: () => {
            this.message.success('Account updated successfully');
            this.isModalVisible = false;
            this.loadAccounts();
          },
          error: (error) => {
            this.message.error('Failed to update account');
            console.error('Error updating account:', error);
          }
        });
      } else {
        // Create account
        this.accountService.createAccount(accountData).subscribe({
          next: () => {
            this.message.success('Account created successfully');
            this.isModalVisible = false;
            this.loadAccounts();
          },
          error: (error) => {
            this.message.error('Failed to create account');
            console.error('Error creating account:', error);
          }
        });
      }
    } else {
      Object.values(this.accountForm.controls).forEach(control => {
        if (control.invalid) {
          control.markAsDirty();
          control.updateValueAndValidity({ onlySelf: true });
        }
      });
    }
  }

  cancelModal(): void {
    this.isModalVisible = false;
  }

  confirmDeactivate(accountId: number, active: boolean): void {
    this.modal.confirm({
      nzTitle: active ? 'Deactivate Account' : 'Activate Account',
      nzContent: active ? 'Are you sure you want to deactivate this account?' : 'Are you sure you want to activate this account?',
      nzOkText: 'Yes',
      nzOkType: 'primary',
      nzOkDanger: active,
      nzOnOk: () => this.toggleAccountActive(accountId),
      nzCancelText: 'No'
    });
  }

  toggleAccountActive(accountId: number): void {
    this.accountService.toggleAccountActive(accountId).subscribe({
      next: () => {
        this.message.success('Account status updated successfully');
        this.loadAccounts();
      },
      error: (error) => {
        this.message.error('Failed to update account status');
        console.error('Error updating account status:', error);
      }
    });
  }

  deleteAccount(accountId: number): void {
    this.accountService.deleteAccount(accountId).subscribe({
      next: () => {
        this.message.success('Account deleted successfully');
        this.loadAccounts();
      },
      error: (error) => {
        this.message.error('Failed to delete account');
        console.error('Error deleting account:', error);
      }
    });
  }
}
