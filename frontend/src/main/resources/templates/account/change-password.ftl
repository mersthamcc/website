<#import "base.ftl" as home>
<#import "../base.ftl" as layout>
<#import "/spring.ftl" as spring />

<@home.homeLayout>
    <div class="card mb-3 mb-lg-5">
        <div class="card-header">
            <h5 class="card-title">Password</h5>
        </div>

        <!-- Body -->
        <div class="card-body">
            <!-- Form -->
            <form>
                <!-- Form Group -->
                <div class="row form-group">
                    <label for="currentPasswordLabel" class="col-sm-3 col-form-label input-label">Current password</label>

                    <div class="col-sm-9">
                        <input type="password" class="form-control" name="currentPassword" id="currentPasswordLabel" placeholder="Enter current password" aria-label="Enter current password">
                    </div>
                </div>
                <!-- End Form Group -->

                <!-- Form Group -->
                <div class="row form-group">
                    <label for="newPassword" class="col-sm-3 col-form-label input-label">New password</label>

                    <div class="col-sm-9">
                        <input type="password" class="form-control" name="newPassword" id="newPassword" placeholder="Enter new password" aria-label="Enter new password">
                    </div>
                </div>
                <!-- End Form Group -->

                <!-- Form Group -->
                <div class="row form-group">
                    <label for="confirmNewPasswordLabel" class="col-sm-3 col-form-label input-label">Confirm new password</label>

                    <div class="col-sm-9">
                        <div class="mb-3">
                            <input type="password" class="form-control" name="confirmNewPassword" id="confirmNewPasswordLabel" placeholder="Confirm your new password" aria-label="Confirm your new password">
                        </div>

                        <h5>Password requirements:</h5>

                        <p class="card-text font-size-1">Ensure that these requirements are met:</p>

                        <ul class="font-size-1">
                            <li>Minimum 8 characters long - the more, the better</li>
                            <li>At least one lowercase character</li>
                            <li>At least one uppercase character</li>
                            <li>At least one number, symbol, or whitespace character</li>
                        </ul>
                    </div>
                </div>
                <!-- End Form Group -->

                <div class="d-flex justify-content-end">
                    <a class="btn btn-white" href="javascript:;">Cancel</a>
                    <span class="mx-2"></span>
                    <button type="submit" class="btn btn-primary">Update Password</button>
                </div>
            </form>
            <!-- End Form -->
        </div>
        <!-- End Body -->
    </div>
</@home.homeLayout>
