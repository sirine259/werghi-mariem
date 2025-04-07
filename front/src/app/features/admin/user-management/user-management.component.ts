import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: any[] = [];
  isLoading = false;
  isModalVisible = false;
  userForm!: FormGroup;
  isEditMode = false;
  currentUserId: number | null = null;

  userRoles = [
    { label: 'Administrator', value: 'ADMIN' },
    { label: 'HR Manager', value: 'HR_MANAGER' },
    { label: 'Employee', value: 'EMPLOYEE' },
    { label: 'Client', value: 'CLIENT' }
  ];

  clientTypes = [
    { label: 'Final Client', value: 'FINAL' },
    { label: 'Intermediary Client', value: 'INTERMEDIARY' }
  ];

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private message: NzMessageService,
    private modal: NzModalService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.initForm();
  }

  loadUsers(): void {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.message.error('Failed to load users');
        this.isLoading = false;
        console.error('Error loading users:', error);
      }
    });
  }

  initForm(): void {
    this.userForm = this.fb.group({
      firstName: [null, [Validators.required]],
      lastName: [null, [Validators.required]],
      email: [null, [Validators.required, Validators.email]],
      password: [null, [Validators.required, Validators.minLength(6)]],
      phone: [null],
      address: [null],
      cin: [null],
      role: ['EMPLOYEE', [Validators.required]],
      active: [true],
      clientType: [null],
      available: [true]
    });
  }

  showModal(user?: any): void {
    this.isEditMode = !!user;

    if (user) {
      this.currentUserId = user.id;
      this.userForm.patchValue({
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phone: user.phone,
        address: user.address,
        cin: user.cin,
        role: user.role,
        active: user.active,
        // Set other fields if available
        clientType: user.clientType,
        available: user.available
      });

      // Remove password validation in edit mode
      this.userForm.get('password')?.clearValidators();
      this.userForm.get('password')?.updateValueAndValidity();
    } else {
      this.currentUserId = null;
      this.userForm.reset({
        active: true,
        role: 'EMPLOYEE',
        available: true
      });

      // Add password validation back in create mode
      this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
      this.userForm.get('password')?.updateValueAndValidity();
    }

    this.isModalVisible = true;
  }

  submitForm(): void {
    if (this.userForm.valid) {
      const userData = this.userForm.value;

      if (this.isEditMode && this.currentUserId) {
        // Update user
        this.userService.updateUser(this.currentUserId, userData).subscribe({
          next: () => {
            this.message.success('User updated successfully');
            this.isModalVisible = false;
            this.loadUsers();
          },
          error: (error) => {
            this.message.error('Failed to update user');
            console.error('Error updating user:', error);
          }
        });
      } else {
        // Create user
        this.userService.createUser(userData).subscribe({
          next: () => {
            this.message.success('User created successfully');
            this.isModalVisible = false;
            this.loadUsers();
          },
          error: (error) => {
            this.message.error('Failed to create user');
            console.error('Error creating user:', error);
          }
        });
      }
    } else {
      Object.values(this.userForm.controls).forEach(control => {
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

  confirmDeactivate(userId: number, active: boolean): void {
    this.modal.confirm({
      nzTitle: active ? 'Deactivate User' : 'Activate User',
      nzContent: active ? 'Are you sure you want to deactivate this user?' : 'Are you sure you want to activate this user?',
      nzOkText: 'Yes',
      nzOkType: 'primary',
      nzOkDanger: active,
      nzOnOk: () => this.toggleUserActive(userId),
      nzCancelText: 'No'
    });
  }

  toggleUserActive(userId: number): void {
    this.userService.toggleUserActive(userId).subscribe({
      next: () => {
        this.message.success('User status updated successfully');
        this.loadUsers();
      },
      error: (error) => {
        this.message.error('Failed to update user status');
        console.error('Error updating user status:', error);
      }
    });
  }

  onRoleChange(role: string): void {
    // Show/hide additional fields based on role
    if (role === 'CLIENT') {
      this.userForm.get('clientType')?.setValidators([Validators.required]);
    } else {
      this.userForm.get('clientType')?.clearValidators();
      this.userForm.get('clientType')?.setValue(null);
    }

    if (role === 'EMPLOYEE') {
      this.userForm.get('available')?.setValue(true);
    } else {
      this.userForm.get('available')?.setValue(null);
    }

    this.userForm.get('clientType')?.updateValueAndValidity();
  }
}
